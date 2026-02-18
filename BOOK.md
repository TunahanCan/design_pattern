# Creational Design Patterns — Mini Book (Java)

> Kısa açıklama: Bu mini kitap, creational (oluşturucu) tasarım desenlerini hızlıca öğrenmen için hazırlanmış, her desen için "Amaç, Problem, Çözüm, Yapı, Örnek, Ne Zaman Kullanılır, Artılar / Eksiler" şablonunu takip eder.

---

**Yazar:** Tunahan Can  
**Versiyon:** 1.0  
**Dil:** Türkçe  
**Repo:** Bu dosya proje kökünde `BOOK.md` olarak bulunur.

[![Build Status](https://img.shields.io/badge/build-local-brightgreen)](./) [![License: CC BY-SA 4.0](https://img.shields.io/badge/license-CC--BY--SA%204.0-blue)](LICENSE)

---

İçindekiler

1. Factory Method
2. Abstract Factory
3. Builder
4. Prototype
5. Singleton

---

Kullanım Notu

- Bu dosya hem düz Markdown olarak okunabilir hem de MkDocs veya GitBook gibi araçlarla statik siteye dönüştürülebilir.
- Her desen için "Örnek" bölümünde proje içindeki Java dosyalarına (örn. `src/main/java/com/can/creational/...`) referans verilir.

---

Nasıl yayınlanır (hızlı kılavuz)

MkDocs (statik site)

```bash
python3 -m pip install --user mkdocs mkdocs-material mkdocs-include-markdown-plugin
mkdocs new . --no-input || true
# mkdocs.yml dosyanızı güncelleyin (ör. material teması) ve docs/ altına bu BOOK.md'i taşıyın veya include edin
mkdocs serve
```

Pandoc (PDF / ePub)

```bash
brew install pandoc
# MacTeX/TeX Live gerekebilir
pandoc BOOK.md -o design-patterns.pdf --pdf-engine=pdflatex
```

---

Şablon: Her desen için takip edilecek yapı

- Amaç (Intent)
- Problem (Problem)
- Çözüm (Solution) — kısa açıklama, gerekli roller (sınıflar/arayüzler)
- Yapı (Structure) — UML / mermaid diyagramı veya kısa class listesi
- Örnek (Example) — projedeki sınıf yollarına referans ve kısa kod snippet (gerekirse include)
- Ne zaman kullanılır? (When to use)
- Artılar / Eksiler (Pros / Cons)

---

## 1) Factory Method

Bu bölüm artık tek bir kitap dosyasında tutulmuyor.
Factory Method içeriği, desenin kendi klasörüne taşındı:

- `src/main/java/com/can/creational/factorymethod/explain/factorymethod.md`

> Not: Böylece her pattern kendi paketinde, ilgili kodla birlikte versiyonlanabilir.

---

## 2) Abstract Factory

Bu bölüm artık tek bir kitap dosyasında tutulmuyor.
Abstract Factory içeriği, desenin kendi klasörüne taşındı:

- `src/main/java/com/can/creational/abstractfactory/explain/abstractfactory.md`

> Not: Böylece her pattern kendi paketinde, ilgili kodla birlikte versiyonlanabilir.

---

## 3) Builder

Bu bölüm artık tek bir kitap dosyasında tutulmuyor.
Builder içeriği, desenin kendi klasörüne taşındı:

- `src/main/java/com/can/creational/builder/explain/builder.md`

> Not: Böylece her pattern kendi paketinde, ilgili kodla birlikte versiyonlanabilir.

---

## 4) Prototype

### Amaç
Mevcut bir nesnenin klonunu oluşturarak yeni nesneler türetmek.

### Problem
Nesne oluşturmanın maliyetli olduğu veya konfigürasyonun karmaşık olduğu durumlarda her seferinde yeniden inşa etmek pahalıdır.

### Çözüm
Bir `Prototype` arayüzü ile `clone()`/`copy()` metodunu tanımla; hazır örneklerden çoğalt.

### Yapı
- Prototype arayüzü (örn. `Prototype`)
- ConcretePrototype (örn. `Resume`) — kopyalama mantığı içerir.

### Örnek
Projede ilgili dosya: `src/main/java/com/can/creational/prototype/PrototypeDemo.java`.

### Ne zaman kullanılır?
- Nesne oluşturma pahalıysa veya çok sayıda benzer nesne üretilecekse.

### Artılar / Eksiler
- Artı: Hızlı türetme, karmaşık konfigürasyonların tekrar kullanılabilmesi.
- Eksi: Derin/kopya problemi (deep vs shallow copy) yönetimi gerekir.

---

## 5) Singleton

Bu bölüm artık tek bir kitap dosyasında tutulmuyor.
Singleton içeriği, desenin kendi klasörüne taşındı:

- `src/main/java/com/can/creational/singleton/explain/singleton.md`

> Not: Böylece her pattern kendi paketinde, ilgili kodla birlikte versiyonlanabilir.

---

Ek bölümler

Contributing

- Fork > Feature branch > Pull request
- Kod örnekleri için JUnit testleri ekle (zorunlu değil ama önerilir)

License

- Bu repo için uygun gördüğün lisansı kök dizine (LICENSE) ekle. Örnek: CC BY-SA 4.0 veya MIT.

---

Son not

Bu şablon, kitabını hem okuyucu dostu bir statik siteye hem de PDF/ePub çıktısına kolayca dönüştürebilmen için düzenlendi. İstersen ben MkDocs yapılandırması (`mkdocs.yml`) ve `docs/` dizinine bölümlere ayrılmış Markdown dosyaları oluşturarak devam edebilirim — hangisini istersin?
