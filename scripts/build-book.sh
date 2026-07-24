#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "$script_dir/.." && pwd)"
frontmatter="$repo_root/docs/book-frontmatter.md"
manifest="$repo_root/docs/chapter-manifest.txt"
output="$repo_root/BOOK.md"
temporary="$(mktemp "$repo_root/.BOOK.md.XXXXXX")"
mode="${1:-write}"

if [[ "$mode" != "write" && "$mode" != "--check" ]]; then
    echo "Kullanım: $0 [--check]" >&2
    exit 2
fi

cleanup() {
    rm -f "$temporary"
}
trap cleanup EXIT

cp "$frontmatter" "$temporary"

chapter_number=0
while IFS='|' read -r chapter_slug relative_path test_path; do
    [[ -z "$chapter_slug" || "$chapter_slug" == \#* ]] && continue
    if [[ -z "$relative_path" || -z "$test_path" ]]; then
        echo "Geçersiz manifest satırı: $chapter_slug" >&2
        exit 1
    fi

    chapter="$repo_root/$relative_path"
    if [[ ! -f "$chapter" ]]; then
        echo "Eksik bölüm: $relative_path" >&2
        exit 1
    fi
    if [[ ! -f "$repo_root/$test_path" ]]; then
        echo "Eksik test: $test_path" >&2
        exit 1
    fi

    chapter_number=$((chapter_number + 1))
    case "$relative_path" in
        */creational/*)
            family_class="creational"
            family_label="OLUŞTURUCU"
            ;;
        */structural/*)
            family_class="structural"
            family_label="YAPISAL"
            ;;
        */behavirol/*)
            family_class="behavioral"
            family_label="DAVRANIŞSAL"
            ;;
        *)
            echo "Bölüm ailesi çözülemedi: $relative_path" >&2
            exit 1
            ;;
    esac
    {
        printf '\n\n<!-- generated-chapter:%02d slug:%s source:%s -->\n' \
            "$chapter_number" "$chapter_slug" "$relative_path"
        printf '<div class="chapter-break"></div>\n\n'
        # Fenced code içeriğine dokunmadan chapter H1-H5 başlıklarını bir seviye indir.
        awk \
            -v chapter_slug="$chapter_slug" \
            -v chapter_number="$chapter_number" \
            -v family_class="$family_class" \
            -v family_label="$family_label" '
            /^[[:space:]]*(```+|~~~+)/ {
                in_fence = !in_fence
                print
                next
            }
            !in_fence && !chapter_title_written && /^# [^#]/ {
                title = substr($0, 3)
                gsub(/&/, "\\&amp;", title)
                gsub(/</, "\\&lt;", title)
                gsub(/>/, "\\&gt;", title)
                printf \
                    "<h2 id=\"chapter-%s\" class=\"pattern-chapter-title family-%s\" " \
                    "data-chapter-label=\"%02d · %s\">%s</h2>\n", \
                    chapter_slug, family_class, chapter_number, family_label, title
                chapter_title_written = 1
                next
            }
            !in_fence && /^#+ / {
                heading = $0
                sub(/ .*/, "", heading)
                if (length(heading) <= 5) {
                    print "#" $0
                    next
                }
            }
            { print }
        ' "$chapter"
    } >> "$temporary"
done < "$manifest"

{
    printf '\n\n---\n\n'
    printf '## Kitabın sonu\n\n'
    printf 'Bir pattern’i öğrenmenin en iyi yolu, onu her yere uygulamak değil; '
    printf 'hangi değişim baskısında işe yaradığını ve hangi bedeli getirdiğini doğru teşhis etmektir.\n'
} >> "$temporary"

if [[ "$mode" == "--check" ]]; then
    if [[ ! -f "$output" ]] || ! cmp -s "$temporary" "$output"; then
        echo "BOOK.md güncel değil; scripts/build-book.sh çalıştırılmalı." >&2
        exit 1
    fi
    echo "BOOK.md güncel: $chapter_number bölüm"
else
    mv "$temporary" "$output"
    trap - EXIT
    echo "BOOK.md üretildi: $chapter_number bölüm"
fi
