# Chain of Responsibility — Sorumluluk Zinciri

> Bu bölüm repodaki çalışan örneği anlatır.
> Güvenlik isimleri taşısa da kod bir eğitim demosudur; production güvenlik katmanı değildir.

## Kod organizasyonu ve composition sınırı

- Desenin üretim kodu `com.can.behavirol.chainofresponsibility` paketindedir.
- Çalıştırılabilir senaryo `com.can.demo.behavioral.chainofresponsibility.ChainOfResponsibilityDemo` sınıfındadır; demo paketi pattern rollerinin parçası değildir.
- Zincirin handler sırası, public `com.can.behavirol.chainofresponsibility.OrderRequestChainFactory` adlı composition factory içinde tanımlıdır. Demo yalnız concrete bağımlılıkları üretip bu factory’yi çağırır; testler de demo içindeki bir yardımcı metoda değil aynı public composition API’sine bağlanır.

Bu ayrım, “deseni kullanan örnek uygulama” ile “desenin yeniden kullanılabilir modeli”ni fiziksel olarak görünür kılar. Gerçek uygulamada en dış composition root; repository, rate limiter ve cache implementasyonlarını configuration/DI üzerinden seçer, factory ise güvenlik açısından anlamlı handler sırasını tek yerde korur.

## 30 saniyelik kart

| Soru | Kısa cevap |
|---|---|
| Niyet | Bir isteği, sırayla çalışan ve gerektiğinde akışı kesen handler nesnelerinden geçirmek |
| Değişen eksen | Kontrollerin türü, sırası ve hangi koşulda zinciri durdurduğu |
| Ana bedel | Akış dağıtık hale gelir; sonuç ve yan etkilerin izini sürmek zorlaşabilir |
| Bu örnekte | Sipariş isteği giriş doğrulama, brute-force, kimlik, temizlik, yetki, tekrar ve işleme adımlarından geçer |
| Hafıza ipucu | “Ben geçiremezsem burada durdurur, geçirebilirsem sıradakine veririm.” |

## Örnek haritası: temel → güçlendirilmiş → production

| Katman | Bu repoda ne var? | Öğrettiği sınır |
|---|---|---|
| Temel örnek | `OrderRequestHandler`, `BaseOrderRequestHandler` ve yedi concrete handler | İsteğin sırayla ilerlemesi ve herhangi bir handler’ın kısa devre yapması |
| Güçlendirilmiş örnek | İlk sınırdaki `RequestValidationHandler` ile `OrderRequestOutcome` | Eksik alanın sonraki handler’da rastlantısal NPE olması yerine typed ret sonucuna dönüşmesi; aynı `false` değerindeki yetki ve duplicate nedenlerinin gözlenmesi |
| Production sınırı | Typed sonuç dönüşü, idempotency key, güvenli kimlik sağlayıcısı, rate limiter ve bounded cache | Boolean ile iş sonucu, güvenlik ve tekrar politikasının tam modellenememesi |

## Akılda kalıcı analoji: havaalanı güvenlik hattı

Bir yolcu uçağa ulaşmadan önce bilet, kimlik, bagaj, pasaport ve kapı kontrollerinden geçer.
Bir istasyon başarısız olursa sonrakiler çalışmaz; her istasyon yalnız kendi kuralını bilir.

Bu üç cümle Sorumluluk Zinciri’nin özüdür.

## Pattern olmasaydı problem ne olurdu?

Tüm kurallar tek metotta toplandığında kod hızla iç içe `if`, erken dönüş ve yan etki yığınına dönüşür.

İlk bakışta kısa görünen bu metot büyüdükçe:

- güvenlik ve iş kuralları birbirine karışır,
- farklı bir akışta tek bir kontrolü yeniden kullanmak zorlaşır,
- kontrol sırası değişiklikleri riskli hale gelir,
- her yeni kural merkezi metodu değiştirmeyi gerektirir,
- hangi adımın isteği mutate ettiği kolayca gözden kaçar.

## Çözüm

Her kontrol ortak bir sözleşmeyi uygulayan ayrı handler olur.
Handler kendi sorumluluğunu yerine getirir ve iki sonuçtan birini seçer:

- isteği reddedip zinciri kesmek,
- `checkNext(request)` ile sonraki handler’a geçirmek.

`BaseOrderRequestHandler`, `next` referansını ve varsayılan geçiş davranışını tek yerde toplar.
`OrderRequestChainFactory`, concrete handler’ları çalışma anında sıraya dizer.
Demo client bu topolojiyi yeniden yazmak yerine factory’den zincir kökünü ister.

### Çözmediği şeyler

Bu desen tek başına:

- doğru güvenlik algoritmasını seçmez,
- handler sırasının güvenli olduğunu garanti etmez,
- transaction veya rollback sağlamaz,
- eşzamanlı erişimi güvenli yapmaz,
- handler dönüş değerinde zengin bir başarı/hata sonucu taşımaz,
- gözlemleme, loglama ve timeout politikasını otomatik kurmaz.

Pattern iletişim yapısını düzenler; iş kurallarının doğruluğu yine geliştiricinin sorumluluğudur.

## Repodaki roller

| Pattern rolü | Repodaki karşılığı | Sorumluluk |
|---|---|---|
| Handler | `OrderRequestHandler` | `setNext` ve `handle` sözleşmesini tanımlar |
| Base Handler | `BaseOrderRequestHandler` | Sonraki handler referansını ve `checkNext` akışını tutar |
| Concrete Handler | `RequestValidationHandler` | Zorunlu alanları sınırda doğrular; eksik isteği typed sonuçla keser |
| Concrete Handler | `BruteForceProtectionHandler` | IP blokluysa zinciri keser |
| Concrete Handler | `AuthenticationHandler` | Kullanıcı/parola kontrolü yapar ve kullanıcıyı request’e yazar |
| Concrete Handler | `DataSanitizationHandler` | Payload’dan `<` ve `>` karakterlerini silip trim eder |
| Concrete Handler | `AuthorizationHandler` | `VIEW_ALL_ORDERS` için admin rolünü kontrol eder |
| Concrete Handler | `CacheHandler` | Daha önce başarılı olmuş imzayı tekrar kabul etmez |
| Terminal Handler | `OrderProcessingHandler` | Sonucu `PROCESSED` yapar, işlemi yazar ve zinciri başarıyla bitirir |
| Request | `OrderRequest` | Zincirde taşınan mutable context |
| Sonuç modeli | `OrderRequestOutcome` | `PROCESSED`, `REJECTED` ve `DUPLICATE` nedenlerini ayırır |
| Destek nesnesi | `LoginAttemptService` | IP başına başarısız deneme sayısını tutar |
| Destek nesnesi | `RequestCache` | Başarılı istek imzalarını `Set` içinde saklar |
| Composition factory | `OrderRequestChainFactory` | Handler’ları güvenlik açısından anlamlı sırada bağlar ve zincir kökünü döndürür |
| Demo composition root | `com.can.demo.behavioral.chainofresponsibility.ChainOfResponsibilityDemo` | Concrete bağımlılıkları üretir ve örnek istekleri gönderir |

## Yapı

```mermaid
classDiagram
    class OrderRequestHandler {
        <<interface>>
        +setNext(next) OrderRequestHandler
        +handle(request) boolean
    }
    class BaseOrderRequestHandler {
        -next OrderRequestHandler
        #checkNext(request) boolean
    }
    class RequestValidationHandler
    class BruteForceProtectionHandler
    class AuthenticationHandler
    class DataSanitizationHandler
    class AuthorizationHandler
    class CacheHandler
    class OrderProcessingHandler
    class OrderRequest {
        -outcome OrderRequestOutcome
        -outcomeMessage String
        +getOutcome() OrderRequestOutcome
        +getOutcomeMessage() String
    }
    class OrderRequestOutcome {
        <<enumeration>>
        PENDING
        PROCESSED
        REJECTED
        DUPLICATE
    }
    class OrderRequestChainFactory {
        <<composition factory>>
        +create(userRepository, loginAttemptService, cache) OrderRequestHandler
    }
    OrderRequestHandler <|.. BaseOrderRequestHandler
    BaseOrderRequestHandler <|-- RequestValidationHandler
    BaseOrderRequestHandler <|-- BruteForceProtectionHandler
    BaseOrderRequestHandler <|-- AuthenticationHandler
    BaseOrderRequestHandler <|-- DataSanitizationHandler
    BaseOrderRequestHandler <|-- AuthorizationHandler
    BaseOrderRequestHandler <|-- CacheHandler
    BaseOrderRequestHandler <|-- OrderProcessingHandler
    OrderRequestHandler ..> OrderRequest : handles
    OrderRequest --> OrderRequestOutcome : current result
    OrderRequestChainFactory ..> OrderRequestHandler : creates ordered chain
```

## Gerçek çalışma akışı

```mermaid
flowchart LR
    R["OrderRequest<br/>PENDING"] --> V[Request validation]
    V -->|alan eksik| X0["false<br/>REJECTED"]
    V -->|geçerli| B[Brute force]
    B -->|bloklu| X1["false<br/>REJECTED"]
    B -->|uygun| A[Authentication]
    A -->|hatalı| X2["false<br/>REJECTED"]
    A -->|başarılı| S[Sanitization]
    S -->|temizlik sonrası boş| X5["false<br/>REJECTED"]
    S -->|geçerli| Z[Authorization]
    Z -->|yetkisiz| X3["false<br/>REJECTED"]
    Z -->|yetkili| C[Cache]
    C -->|imza var| X4["false<br/>DUPLICATE"]
    C -->|yeni| P[Order processing]
    P --> OK["true<br/>PROCESSED"]
```

## Kod execution trace

### 1. Normal kullanıcının sipariş oluşturması

1. Validation bütün zorunlu alanların mevcut olduğunu doğrular.
2. IP henüz bloklu değildir.
3. `"can" / "1234"` doğrulanır.
4. `authenticatedUser` request üzerine yazılır.
5. `<script>...` payload’ındaki açı parantezleri silinir.
6. `CREATE_ORDER` admin gerektirmez.
7. İmza cache’de bulunmaz.
8. İşlem mesajı yazılır ve zincir `true` döner.
9. Cache başarılı imzayı saklar.

### 2. Normal kullanıcının tüm siparişleri istemesi

1. Validation, brute-force ve authentication adımları geçilir.
2. Payload, authorization’dan önce mutate edilir.
3. Kullanıcı admin olmadığı için authorization `false` döndürür.
4. Cache ve order processing çalışmaz.

### 3. Aynı admin isteğinin ikinci kez gelmesi

1. İkinci istek yine validation, brute-force, authentication, sanitization ve authorization’dan geçer.
2. Cache imzayı bulur.
3. Order processing çağrılmaz.
4. Mevcut API cache-hit için de `false` döndürür.

Bu son nokta önemlidir: örnek gerçek bir “sonuç cache’i” değil, tekrar bastırma mekanizmasıdır.

## API, invariant ve sonuç semantiği

- Zincirin kökü `OrderRequestChainFactory.create(...)` tarafından döndürülen `RequestValidationHandler`dır.
- Factory zorunlu `UserRepository`, `LoginAttemptService` ve `RequestCache` bağımlılıklarını null kabul etmez.
- Null request sonuç yazılabilecek context olmadığı için açık `IllegalArgumentException` üretir.
- Null/blank username, password, IP veya payload ile null operation `REJECTED` olur; sonraki handler’lar çağrılmaz.
- Validation ilk hatayı deterministik alan sırasıyla raporlar.
- Başta dolu olup sanitization sonrasında boşalan payload `REJECTED` olur; authorization ve işleme adımlarına ilerlemez.
- `setNext` verilen handler’ı döndürür; bu yüzden fluent bağlantı kurulabilir.
- `checkNext`, sıradaki handler yoksa `true` döndürür.
- Bir handler `false` döndürdüğünde kalan zincir çalışmaz.
- `OrderProcessingHandler` terminaldir; yanlışlıkla arkasına handler bağlansa bile onu çağırmaz.
- `OrderRequest` immutable değildir; payload ve authenticated user değişebilir.
- Cache imzası `username|operation|sanitizedPayload` biçimindedir.
- İmza IP, parola veya ayrı bir idempotency key içermez.
- Başarısız istekler cache’e yazılmaz.
- Brute-force eşiği en az 1 olmalıdır; geçersiz configuration constructor’da reddedilir.
- `boolean` yalnız “terminal işleme kabul edildi mi?” bilgisini taşır.
- Authentication, authorization, blok ve cache-hit dönüşte aynı `false` değerinde birleşir.
- Buna karşılık request üzerindeki `outcome`, reddi duplicate kısa devreden ayırır.
- `outcomeMessage`, demosal ve insan okunur nedeni taşır; localization/domain error code değildir.

Production API’sinde sonucu mutable request’e yazmak yerine, handler’dan typed bir `HandlingResult` döndürmek daha açık olur.

## Test kontrat haritası

| `@Nested` grup | Korunan davranış |
|---|---|
| `RequestValidation` | Public composition factory’nin validation-first kökü; eksik alanın zincirin başında typed ret olması; null request ve geçersiz brute-force eşiğinin fail-fast davranışı |
| `SuccessfulRequests` | Create başarısı, admin erişimi, payload mutation |
| `RejectedRequests` | Yanlış parola, yetki reddi, deneme eşiği, başarılı login reset’i ve sanitization sonrası boş payload |
| `CacheShortCircuit` | İlk çağrı/tekrar sonucu ve payload’a bağlı imza |
| `OutcomeSemantics` | Başarı/reddetme/tekrar ayrımı ve processing handler’ın terminal olması |

Testler sunum davranışını ölçmek için stdout’a bağımlı olmamalıdır.
Terminal davranış, yanlışlıkla bağlanan recording handler’ın çağrılmadığı gözlenerek korunur.

## Edge case, güvenlik, concurrency ve performans

### Audit sırasında görülen riskler

- Üçüncü hatalı giriş sayacı eşiğe getirir; brute-force reddi sonraki istekte görülür.
- Başarılı giriş, aynı IP’deki bütün hata sayısını sıfırlar.
- Validation yalnız zorunlu alan varlığını doğrular; uzunluk, IP biçimi ve payload şeması hâlâ kontrol edilmez.
- Null request typed outcome’a yazılamadığı için exception politikasına gider.
- Sanitization sonrası boşluk yeniden doğrulanır ve zincir bu adımda kesilir.
- `setNext(null)` fluent kurulumun sonraki çağrısını bozabilir.
- `HashMap` ve `HashSet` thread-safe değildir.
- Düz metin parola karşılaştırması production için uygun değildir.
- Açı parantezi silmek güvenli HTML/XSS sanitizasyonu değildir.
- Cache sınırsız büyür; eviction, TTL ve tenant ayrımı yoktur.
- Handler başına çağrı maliyeti O(1) kabul edilirse zincir O(h) çalışır.

Production’da güvenilir kimlik sağlayıcısı, parola hash’i, güvenli encoder, rate limiter, bounded cache ve thread-safe depolar kullanılmalıdır.

## Ne zaman kullan?

- Filtrelerin sırası çalışma anında kurulacaksa,
- her kontrol bağımsız yeniden kullanılacaksa,
- herhangi bir adım akışı kısa devre edebilecekse,
- middleware, validation veya approval hattı modelleniyorsa,
- gönderen concrete işleyiciyi bilmemeliyse.

## Ne zaman kullanma?

- Tek ve sabit bir kontrol varsa,
- bütün adımlar atomik transaction gerektiriyorsa,
- iş akışı dallanma, retry ve uzun süreli state taşıyorsa,
- sonuç nedeninin güçlü biçimde modellenmesi gerekirken yalnız boolean kullanılacaksa,
- sıra gizlenince sistem anlaşılmaz hale geliyorsa.

## Artılar ve bedeller

| Artı | Bedel |
|---|---|
| Handler’lar tek sorumluluğa yaklaşır | Execution flow birden fazla dosyaya dağılır |
| Zincir runtime’da kurulabilir | Yanlış sıra güvenlik açığı veya gereksiz yan etki doğurabilir |
| Yeni kontrol ayrı sınıf olarak eklenebilir | Composition kodu yeni handler için yine değişebilir |
| Erken çıkış doğaldır | Bir isteğin neden durduğu zengin sonuç yoksa kaybolur |
| Handler yeniden kullanılabilir | Paylaşılan mutable servisler concurrency riski taşır |

## En çok karışan desen: Decorator

| Chain of Responsibility | Decorator |
|---|---|
| Bir handler isteği durdurabilir | Decorator çoğunlukla wrapped nesneyi çağırır |
| Amaç uygun işleyici/filtre hattıdır | Amaç nesneye davranış eklemektir |
| Sonraki adım opsiyoneldir | İç nesne dekorasyonun merkezidir |
| Sonuç kısa devre olabilir | Çağrı genellikle içeri ve tekrar dışarı akar |

Bu repo örneği “her handler katkı yapıp sıradakine geçirir” biçimiyle middleware’e de benzer.

## Yaygın hatalar

1. Handler sırasını önemsiz sanmak.
2. Bütün handler’lara ortak mutable state vermek.
3. `false` sonucunun nedenini kaybetmek.
4. Her handler’da farklı exception politikası kullanmak.
5. Terminal handler’dan sonra yeni kontrol bağlamak.
6. Güvenlik demosunu production güvenliği sanmak.
7. Cache ile duplicate suppression kavramlarını karıştırmak.

## Alıştırmalar

### Seviye 1 — Gözlemle

`LoggingHandler` ekle ve request’in zincirde hangi sırayla ilerlediğini bir listeye yaz.
Mevcut handler’ların kodunu değiştirmeden listenin beklenen sırada olduğunu test et.

### Seviye 2 — Tasarla

`boolean` yerine sealed bir `ChainResult` tasarla.
Başarı, authentication hatası, yetki hatası, blok ve duplicate sonuçlarını ayrı modelle.

### Seviye 3 — Production’a yaklaştır

IP ve kullanıcı anahtarlı, zaman pencereli bir rate limiter tasarla.
Thread safety, TTL, saat bağımlılığı ve test edilebilir clock kararlarını dokümante et.

## Hafıza cümlesi

**Sorumluluk Zinciri, isteği “kim yapacak?” diye merkezi koşullarda aramaz; sıraya dizilmiş uzmanlara tek tek sorar ve biri dur dediğinde akışı keser.**
