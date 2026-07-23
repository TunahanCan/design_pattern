# Design Pattern Lab — Java 21

Bu depo, 22 GoF tasarım desenini küçük ama çalışan Java örnekleriyle öğrenmek için hazırlanmış bir laboratuvardır. Her pattern üç katmanda ele alınır:

- **Kod:** desenin sınıf işbirliğini gösteren küçük domain örneği
- **Test:** JUnit 5, `@Nested` ve senaryo adlarıyla okunabilir davranış sözleşmeleri
- **Doküman:** problem, çözüm, trade-off, diyagram, production notu ve alıştırmalar

## Okumaya başla

- [Birleşik Markdown kitap](BOOK.md)
- [Baskıya hazır görsel PDF](docs/design-patterns-java.pdf)

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
ve process `2` koduyla kapanır. Katalog, mevcut demo sınıflarını değiştirmeden
onları aile ve niyet bakımından tek bir composition root altında gruplar.

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

GoF kataloğundaki `Interpreter` bu repoda yer almaz. Davranışsal paket yolu geçmişten gelen yazımıyla `com.can.behavirol` olarak korunmuştur.

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

PDF üretimi yerel Chrome/Chromium ve Mermaid 11 kullanır. `BOOK.md`, 22 ayrı `explain/*.md` bölümünden deterministik olarak oluşturulur.
Render aracı Node.js 22+ gerektirir ve Mermaid/Markdown renderer sürümlerini çalıştırma sırasında indirir.
Outline ve yapısal PDF denetimi için macOS üzerindeki Swift/PDFKit de gereklidir.
İçerik validator'ı kanonik pattern envanterini, manifest/katalog/atlas sırasını
ve Markdown fence yapısını denetler. Mermaid sözdiziminin gerçek parse/render
doğrulaması `render-book.mjs` aşamasında yapılır.
