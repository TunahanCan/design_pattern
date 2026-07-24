# Design Pattern Lab — Java 21

Bu depo, 22 GoF tasarım desenini küçük ama çalışan Java örnekleriyle öğrenmek için hazırlanmış bir laboratuvardır. Her pattern üç katmanda ele alınır:

- **Kod:** desenin sınıf işbirliğini gösteren küçük domain örneği
- **Demo:** domain nesne graph’ını kuran, çalıştırılabilir composition root
- **Test:** JUnit 5, `@Nested` ve senaryo adlarıyla okunabilir davranış sözleşmeleri
- **Doküman:** problem, çözüm, trade-off, diyagram, production notu ve alıştırmalar

Kitabın giriş kısmı ayrıca desenleri tek tek ezberletmek yerine, kavramsal bir
sipariş akışında Adapter, Facade, Chain, Strategy, Command, State ve Observer
sınırlarının nasıl birlikte çalıştığını; hangi noktada gereksiz “pattern
çorbası” oluştuğunu üç görsel üzerinden anlatır.

## Okumaya başla

- [Birleşik Markdown kitap](BOOK.md)
- [Baskıya hazır görsel PDF](docs/design-patterns-java.pdf)
- [Etkileşimli ve çevrimdışı HTML kitap](docs/design-patterns-java.html)
- [Yaratımsal desenler için çevrimdışı görsel karar panosu](src/main/java/com/can/creational/explain/creational-visual-guide.html)

PDF sürümü baskı ve not alma için statik, yüksek çözünürlüklü Mermaid
diyagramları kullanır. HTML sürümünde aynı diyagramların bağlantıları
görünürlük durumuna göre hafifçe vurgulanır; işletim sistemindeki
`prefers-reduced-motion` tercihi açıksa animasyon otomatik kapanır.

## Çalıştır

Proje Java 21 hedefler:

```bash
java -version
mvn -version
mvn test
mvn compile exec:java -Dexec.mainClass=com.can.Main
```

Örnekler artık merkezi katalogdan seçilebilir:

```bash
# Kataloğu listele
mvn compile exec:java -Dexec.mainClass=com.can.Main -Dexec.args=--list

# Yalnızca bir GoF ailesini çalıştır
mvn compile exec:java -Dexec.mainClass=com.can.Main -Dexec.args=structural

# Yalnızca bir pattern'i çalıştır
mvn compile exec:java -Dexec.mainClass=com.can.Main -Dexec.args=factory-method
```

`all`, `creational`, `structural`, `behavioral`, ailelerin Türkçe adları veya
katalogda görünen bir pattern slug'ı kullanılabilir. `--help` ve `--list`
kataloğu gösterir. Fazla argüman ya da bilinmeyen seçim CLI kullanım hatasıdır
ve process `2` koduyla kapanır. Katalog, `com.can.demo` altındaki composition
root’larını aile ve niyet bakımından tek noktada gruplar.

macOS/Homebrew üzerinde Maven Java 17 ile açılıyorsa:

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
export PATH="$(brew --prefix openjdk@21)/bin:$PATH"
```

## Kapsam

| Aile | Pattern’ler |
|---|---|
| Creational | Factory Method, Abstract Factory, Builder, Prototype, Singleton |
| Structural | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| Behavioral | Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor |

GoF kataloğundaki `Interpreter` bu repoda yer almaz. Davranışsal domain paket
yolu geçmişten gelen yazımıyla `com.can.behavirol` olarak korunmuş; yeni demo
sınırı doğru yazımla `com.can.demo.behavioral` altında kurulmuştur.

## Paket sınırı

```text
src/main/java/com/can/
├── demo/
│   ├── creational/     # 5 çalıştırılabilir composition root
│   ├── structural/     # 7 çalıştırılabilir composition root
│   └── behavioral/     # 10 çalıştırılabilir composition root
├── creational/         # pattern/domain kodu
├── structural/         # pattern/domain kodu
└── behavirol/          # pattern/domain kodu (tarihsel paket adı)

src/test/java/com/can/   # domain davranış sözleşmeleri ve katalog/CLI testleri
```

Bağımlılık yönü `demo → domain` şeklindedir. Domain kodu demo paketini bilmez;
testler de sunum çıktısı yerine domain sözleşmelerini doğrudan kurup doğrular.

Her pattern bölümü örnekleri üç seviyede okur:

1. **Temel örnek:** Pattern rollerini görünür tutan en küçük çalışan akış.
2. **Güçlendirilmiş örnek:** Gerçek hayatta karşılaşılacak invariant, hata veya
   değişim baskısını kod ve testle görünür kılan sürüm.
3. **Production sınırı:** Demo kapsamına bilinçli olarak alınmayan güvenlik,
   concurrency, kalıcılık ve operasyon sorumlulukları.

## Kitabı yeniden üret

```bash
./scripts/build-book.sh
node scripts/validate-learning-content.mjs
node scripts/render-book.mjs
```

PDF ve çevrimdışı HTML üretimi yerel Chrome/Chromium ve Mermaid 11 kullanır.
`BOOK.md`, 22 ayrı `explain/*.md` bölümünden deterministik olarak oluşturulur.
Render aracı Node.js 22+ ile npm/npx gerektirir ve Mermaid/Markdown renderer sürümlerini çalıştırma sırasında indirir.
macOS’ta özel bölüm outline’ı ve yapısal denetim Swift/PDFKit ile yapılır.
Diğer platformlarda Chrome’un erişilebilir outline çıktısı; `pdfinfo`,
`pdftotext` ve `pdftohtml` kullanan taşınabilir denetimden geçirilir.
İçerik validator'ı kanonik pattern envanterini, manifest/katalog/atlas sırasını
demo–domain paket sınırını ve Markdown fence yapısını denetler. Mermaid
sözdiziminin gerçek parse/render doğrulaması `render-book.mjs` aşamasında
yapılır. HTML’de hareket yalnız ekran ortamında etkinleşir; PDF üretiminde tüm
animasyonlar bilinçli olarak sabitlenir.
