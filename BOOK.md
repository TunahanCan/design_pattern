# Design Patterns Kitabı (Java Projesi)

Bu doküman, projede uygulanan tasarım desenlerini tek bir yerden, kısa-öz ama tam içerikle anlatır.

---

## 1) Giriş

### Design Pattern nedir?
Design Pattern (tasarım deseni), yazılımda tekrar eden bir problemi kanıtlanmış bir yaklaşımla çözmek için kullanılan, isimlendirilmiş bir tasarım şablonudur. Pattern bir "kod parçası" değil; problem-çözüm eşlemesi ve rol dağılımıdır.

### Neden kullanılır?
- Ortak bir dil sağlar (ekip içinde "Factory Method kullanalım" gibi net iletişim).
- Kodun bakımını ve genişletilmesini kolaylaştırır.
- Sorumlulukları ayrıştırır; bağımlılığı azaltır.
- Yeni geliştiricinin projeyi daha hızlı anlamasına yardımcı olur.

---

## 2) Pattern Aileleri

Bu repodaki pattern’ler üç ailede toplanır:

- **Creational** (`src/main/java/com/can/creational/**`)
- **Structural** (`src/main/java/com/can/structural/**`)
- **Behavioral** (`src/main/java/com/can/behavirol/**`)

---

# A) Creational Patterns

## A.1 Factory Method
**Derin okuma:** [`factorymethod.md`](src/main/java/com/can/creational/factorymethod/explain/factorymethod.md)

- **Amaç (Intent):** Nesne üretimini alt sınıflara devrederek, istemciyi somut sınıflardan bağımsızlaştırmak.
- **Problem:** İstemci kodu doğrudan `new` ile somut sınıf oluşturduğunda, yeni kanal/tip eklendikçe kod değişir.
- **Çözüm:** `NotificationCreator` gibi bir creator soyutlamasıyla üretimi kapsüllemek; `EmailNotificationCreator`, `SmsNotificationCreator`, `PushNotificationCreator` gibi somut üreticilerle nesneyi seçmek.
- **Yapı / Roller:**
  - Product: `Notification`
  - ConcreteProduct: `EmailNotification`, `SmsNotification`, `PushNotification`
  - Creator: `NotificationCreator`
  - ConcreteCreator: `EmailNotificationCreator`, `SmsNotificationCreator`, `PushNotificationCreator`
  - Client/Application: `NotificationService`, `FactoryMethodDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.creational.factorymethod`
  - Öne çıkan sınıflar: `NotificationService`, `NotificationCreator`, `FactoryMethodDemo`
- **Akış (adım adım):**
  1. İstemci `NotificationRequest` ile kanal bilgisini verir.
  2. `NotificationService`, uygun creator’ı seçer.
  3. Creator, uygun `Notification` nesnesini üretir.
  4. `NotificationSender` üzerinden gönderim tamamlanır.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Üretilecek nesne tipi çalışma anında değişiyorsa.
  - Kullanılmaz: Tek bir somut sınıf varsa ve varyasyon yoksa.
- **Artılar / Eksiler:**
  - Artı: Açık/Kapalı ilkesine uygun genişleme.
  - Eksi: Sınıf sayısı artar.
- **Kısa gerçek hayat analojisi:** Bankada "işlem türüne göre" farklı gişeye yönlendirilmek.

## A.2 Abstract Factory
**Derin okuma:** [`abstractfactory.md`](src/main/java/com/can/creational/abstractfactory/explain/abstractfactory.md)

- **Amaç (Intent):** Birbiriyle ilişkili ürün ailelerini, somut sınıfları bilmeden üretmek.
- **Problem:** Tema/aile değiştikçe buton-checkbox gibi bütün UI bileşenlerinin birlikte tutarlı değişmesi gerekir.
- **Çözüm:** `GuiFactory` arayüzü ile ürün ailesi üretimi tanımlanır; `LightThemeFactory` ve `DarkThemeFactory` gibi somut fabrikalar aileyi birlikte üretir.
- **Yapı / Roller:**
  - AbstractFactory: `GuiFactory`
  - ConcreteFactory: `LightThemeFactory`, `DarkThemeFactory`
  - AbstractProduct: `Button`, `Checkbox`
  - ConcreteProduct: `LightButton`, `DarkButton`, `LightCheckbox`, `DarkCheckbox`
  - Client: `UiScreen`, `AbstractFactoryDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.creational.abstractfactory`
  - Öne çıkan sınıflar: `GuiFactoryProvider`, `UiScreen`, `Theme`
- **Akış (adım adım):**
  1. Tema (`Theme`) seçilir.
  2. `GuiFactoryProvider` uygun fabrikayı döner.
  3. `UiScreen` buton ve checkbox’ı aynı aileden üretir.
  4. Ekran tutarlı görsel dille render edilir.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Birlikte değişmesi gereken ürün aileleri varsa.
  - Kullanılmaz: Tek ürün tipi varsa, Factory Method yeterli olabilir.
- **Artılar / Eksiler:**
  - Artı: Aile tutarlılığı garanti edilir.
  - Eksi: Yeni ürün tipi eklemek tüm fabrikaları etkiler.
- **Kısa gerçek hayat analojisi:** Aynı mobilya serisinden koltuk + masa + sandalye seti almak.

## A.3 Builder
**Derin okuma:** [`builder.md`](src/main/java/com/can/creational/builder/explain/builder.md)

- **Amaç (Intent):** Karmaşık nesne kurulumunu adım adım yönetmek.
- **Problem:** Çok parametreli kurucular okunmaz ve hata riski taşır.
- **Çözüm:** `Report.Builder` ile adımlı kurulum, `ReportDirector` ile standart reçeteler.
- **Yapı / Roller:**
  - Product: `Report`
  - Builder: `Report.Builder`
  - Director: `ReportDirector`
  - Client: `BuilderDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.creational.builder`
  - Öne çıkan sınıflar: `Report`, `ReportDirector`, `BuilderDemo`
- **Akış (adım adım):**
  1. Builder oluşturulur.
  2. Gerekli alanlar adım adım set edilir.
  3. Opsiyonel alanlar eklenir.
  4. `build()` ile immutable/nihai nesne alınır.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Opsiyonel alan sayısı fazla ve kurulum adımlıysa.
  - Kullanılmaz: Nesne çok basitse düz constructor yeterlidir.
- **Artılar / Eksiler:**
  - Artı: Okunabilir kurulum, validasyon noktası.
  - Eksi: Ek sınıf/kod maliyeti.
- **Kısa gerçek hayat analojisi:** Arabayı paket paket seçerek sipariş etmek.

## A.4 Prototype
**Derin okuma:** [`prototype.md`](src/main/java/com/can/creational/prototype/explain/prototype.md)

- **Amaç (Intent):** Yeni nesneleri, mevcut örneklerin kopyasından üretmek.
- **Problem:** Sıfırdan üretim maliyetli veya konfigürasyon karmaşık olabilir.
- **Çözüm:** `Prototype` kontratıyla klonlama; `CandidateProfileRegistry` gibi bir kayıt üzerinden örnek çoğaltma.
- **Yapı / Roller:**
  - Prototype: `Prototype`
  - ConcretePrototype: `CandidateProfile`
  - Registry: `CandidateProfileRegistry`
  - Client: `PrototypeDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.creational.prototype`
  - Öne çıkan sınıflar: `CandidateProfile`, `Address`, `CandidateProfileRegistry`
- **Akış (adım adım):**
  1. Örnek profil registry’ye alınır.
  2. İstemci uygun prototipi seçer.
  3. Klon oluşturulur.
  4. Gerekli küçük farklılıklar klonda güncellenir.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Benzer ama çok sayıda nesne hızlı üretiliyorsa.
  - Kullanılmaz: Derin kopya ihtiyacı net değilse ve hata riski yüksekse.
- **Artılar / Eksiler:**
  - Artı: Üretim maliyeti azalır.
  - Eksi: Deep/shallow copy yönetimi zorlaşabilir.
- **Kısa gerçek hayat analojisi:** Hazır CV şablonunu kopyalayıp kişiye göre düzenlemek.

## A.5 Singleton
**Derin okuma:** [`singleton.md`](src/main/java/com/can/creational/singleton/explain/singleton.md)

- **Amaç (Intent):** Bir sınıfın tek örneğini garanti etmek ve global erişim noktası sağlamak.
- **Problem:** Konfigürasyon gibi paylaşımlı bir kaynağın birden fazla örneği tutarsızlık üretir.
- **Çözüm:** Özel constructor + statik erişim metodu ile tek örnek yönetimi (`AppConfig`).
- **Yapı / Roller:**
  - Singleton: `AppConfig`
  - Client: `SingletonDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.creational.singleton`
  - Öne çıkan sınıflar: `AppConfig`, `SingletonDemo`
- **Akış (adım adım):**
  1. İstemci `getInstance()` çağırır.
  2. İlk çağrıda nesne oluşturulur.
  3. Sonraki çağrılarda aynı örnek döner.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Tekil kaynak/konfigürasyon yönetiminde.
  - Kullanılmaz: Yoğun test izolasyonu gereken, global state’in riskli olduğu senaryolarda.
- **Artılar / Eksiler:**
  - Artı: Tekil ve merkezi yönetim.
  - Eksi: Gizli global bağımlılık oluşturabilir.
- **Kısa gerçek hayat analojisi:** Binadaki tek merkezi elektrik panosu.

---

# B) Structural Patterns

## B.1 Adapter
**Derin okuma:** [`adapter.md`](src/main/java/com/can/structural/adapter/explain/adapter.md)

- **Amaç (Intent):** Uyuşmayan arayüzleri birlikte çalıştırmak.
- **Problem:** `RoundHole` sadece `RoundPeg` kabul ederken sistemde `SquarePeg` vardır.
- **Çözüm:** `SquarePegAdapter`, kare pimi dairesel arayüze çevirir.
- **Yapı / Roller:**
  - Target: `RoundPeg`
  - Adaptee: `SquarePeg`
  - Adapter: `SquarePegAdapter`
  - Client: `RoundHole`, `AdapterPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.structural.adapter`
  - Öne çıkan sınıflar: `RoundHole`, `SquarePegAdapter`
- **Akış (adım adım):**
  1. Kare pim adapte edilir.
  2. Adaptör, eşdeğer yarıçap hesabı yapar.
  3. `RoundHole` target arayüzüyle doğrulama yapar.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Eski/yeni sistem arayüzleri çakıştığında.
  - Kullanılmaz: Arayüzleri doğrudan değiştirmek mümkünse.
- **Artılar / Eksiler:**
  - Artı: Mevcut kodu kırmadan entegrasyon.
  - Eksi: Ek soyutlama katmanı.
- **Kısa gerçek hayat analojisi:** Priz dönüştürücü adaptör.

## B.2 Bridge
**Derin okuma:** [`bridge.md`](src/main/java/com/can/structural/bridge/explain/bridge.md)

- **Amaç (Intent):** Soyutlama ile implementasyonu ayırıp bağımsız genişletmek.
- **Problem:** Kumanda türleri ve cihaz türleri birlikte arttığında sınıf patlaması olur.
- **Çözüm:** `RemoteControl` soyutlaması ile `Device` implementasyonu ayrılır; `AdvancedRemoteControl` farklı davranış ekler.
- **Yapı / Roller:**
  - Abstraction: `RemoteControl`
  - RefinedAbstraction: `AdvancedRemoteControl`
  - Implementor: `Device`
  - ConcreteImplementor: `Tv`, `Radio`
  - Client: `BridgePatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.structural.bridge`
  - Öne çıkan sınıflar: `RemoteControl`, `Device`, `Tv`, `Radio`
- **Akış (adım adım):**
  1. Kumanda bir `Device` ile eşleştirilir.
  2. Kumanda çağrıları cihaz implementasyonuna delege edilir.
  3. Yeni cihaz veya yeni kumanda bağımsız eklenir.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: İki eksenli varyasyon varsa (örn. kumanda x cihaz).
  - Kullanılmaz: Tek eksen varsa gereksiz karmaşıklık oluşturabilir.
- **Artılar / Eksiler:**
  - Artı: Kombinasyon patlamasını önler.
  - Eksi: Başlangıçta yapı daha soyut görünür.
- **Kısa gerçek hayat analojisi:** Evrensel kumandanın farklı marka TV/Radio ile çalışması.

---

# C) Behavioral Patterns

## C.1 Chain of Responsibility
**Derin okuma:** [`chainofresponsibility.md`](src/main/java/com/can/behavirol/chainofresponsibility/explain/chainofresponsibility.md)

- **Amaç (Intent):** İsteği bir işlem zincirinden geçirip sorumluluğu adım adım dağıtmak.
- **Problem:** Doğrulama/kimlik/izin/cache gibi kontroller tek metotta birikirse yönetilmesi zorlaşır.
- **Çözüm:** `OrderRequestHandler` zinciri; her handler kendi kontrolünü yapar ve devam ettirir.
- **Yapı / Roller:**
  - Handler: `OrderRequestHandler`, `BaseOrderRequestHandler`
  - ConcreteHandler: `AuthenticationHandler`, `AuthorizationHandler`, `DataSanitizationHandler`, `BruteForceProtectionHandler`, `CacheHandler`, `OrderProcessingHandler`
  - Request: `OrderRequest`
  - Client: `ChainOfResponsibilityDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.chainofresponsibility`
  - Destek sınıfları: `UserRepository`, `LoginAttemptService`, `RequestCache`
- **Akış (adım adım):**
  1. İstek zincirin ilk halkasına gelir.
  2. Her handler kendi kuralını uygular.
  3. Başarılıysa bir sonraki halkaya geçer.
  4. Bir adım reddederse akış durur.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Sıralı, ayrık kontroller gerektiğinde.
  - Kullanılmaz: Tek bir basit kontrol varsa.
- **Artılar / Eksiler:**
  - Artı: Esnek boru hattı kurulur.
  - Eksi: Hata ayıklamada zincir takibi gerekir.
- **Kısa gerçek hayat analojisi:** Havalimanı güvenlik aşamaları.

## C.2 Command
**Derin okuma:** [`command.md`](src/main/java/com/can/behavirol/command/explain/command.md)

- **Amaç (Intent):** İstekleri nesneleştirerek kuyruklama, loglama, geri alma (undo) desteği sağlamak.
- **Problem:** UI eylemleri doğrudan iş koduna bağlıysa undo/redo ve izleme zorlaşır.
- **Çözüm:** Her işlem bir `Command` sınıfına taşınır; `CommandHistory` ile geçmiş yönetilir.
- **Yapı / Roller:**
  - Command: `Command`, `AbstractEditorCommand`
  - ConcreteCommand: `CopyCommand`, `PasteCommand`, `WriteTextCommand`, `UndoCommand`
  - Receiver: `Editor`
  - Invoker: `EditorToolbar`
  - Client: `ApplicationContext`, `CommandPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.command`
  - Öne çıkan sınıflar: `Editor`, `CommandHistory`
- **Akış (adım adım):**
  1. UI bir command nesnesi üretir.
  2. Invoker command’i çalıştırır.
  3. Receiver iş mantığını uygular.
  4. Geçmişe eklenir, gerektiğinde undo yapılır.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Undo/redo veya işlem kuyruğu gerekiyorsa.
  - Kullanılmaz: Çok basit tek adımlı aksiyonlarda.
- **Artılar / Eksiler:**
  - Artı: Gevşek bağlı, genişleyebilir eylem modeli.
  - Eksi: Çok sayıda küçük command sınıfı oluşur.
- **Kısa gerçek hayat analojisi:** TV kumandasındaki her tuşun ayrı komut olması.

## C.3 Iterator
**Derin okuma:** [`iterator.md`](src/main/java/com/can/behavirol/iterator/explain/iterator.md)

- **Amaç (Intent):** Koleksiyonun iç yapısını açmadan elemanları sıralı dolaşmak.
- **Problem:** Farklı veri kaynaklarında gezinme mantığı istemciye dağılır.
- **Çözüm:** `ProfileIterator`/`SocialGraphIterator` ile standart gezinme arayüzü.
- **Yapı / Roller:**
  - Iterator: `ProfileIterator`
  - ConcreteIterator: `SocialGraphIterator`
  - Aggregate: `SocialNetwork`
  - ConcreteAggregate: `Facebook`, `SocialGraph`
  - Client: `SocialSpammer`, `IteratorPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.iterator`
  - Öne çıkan sınıflar: `SocialGraph`, `Profile`, `RelationType`
- **Akış (adım adım):**
  1. İstemci uygun iterator ister.
  2. Iterator durumunu içeride tutar.
  3. `hasNext/next` benzeri akışla elemanlar gezilir.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Aynı veri üzerinde farklı dolaşım stratejileri varsa.
  - Kullanılmaz: Basit liste döngüsü yeterliyse.
- **Artılar / Eksiler:**
  - Artı: Gezinme mantığı merkezileşir.
  - Eksi: Ek soyutlama katmanı getirir.
- **Kısa gerçek hayat analojisi:** Müzik çalardaki sonraki/önceki parça gezintisi.

## C.4 Mediator
**Derin okuma:** [`mediator.md`](src/main/java/com/can/behavirol/mediator/explain/mediator.md)

- **Amaç (Intent):** Nesneler arası karmaşık iletişimi merkezi bir arabulucuda toplamak.
- **Problem:** UI bileşenleri birbirine doğrudan bağlıysa bağımlılık ağı oluşur.
- **Çözüm:** `Mediator`/`AuthenticationDialog` bileşenler arası iletişimi yönetir.
- **Yapı / Roller:**
  - Mediator: `Mediator`
  - ConcreteMediator: `AuthenticationDialog`
  - Colleague: `Component`, `Button`, `Checkbox`, `Textbox`, `Label`
  - Client: `MediatorPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.mediator`
  - Öne çıkan sınıflar: `AuthenticationDialog`, `Component`
- **Akış (adım adım):**
  1. Bileşen olay üretir.
  2. Olay mediatore bildirilir.
  3. Mediator diğer bileşenlerin durumunu günceller.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Çok bileşenli form/diyalog koordinasyonunda.
  - Kullanılmaz: Bileşen sayısı az ve iletişim çok basitse.
- **Artılar / Eksiler:**
  - Artı: Bileşenler arası bağımlılığı azaltır.
  - Eksi: Mediator zamanla çok büyüyebilir.
- **Kısa gerçek hayat analojisi:** Trafiği yöneten kavşak kontrol merkezi.

## C.5 Memento
**Derin okuma:** [`memento.md`](src/main/java/com/can/behavirol/memento/explain/memento.md)

- **Amaç (Intent):** Nesnenin iç durumunu dışarı açmadan anlık görüntü alıp geri yüklemek.
- **Problem:** Undo ihtiyacında nesnenin içini dış dünyaya açmak kapsüllemeyi bozar.
- **Çözüm:** `TextEditor` durumunu memento olarak saklar; `EditorHistory` geçmişi yönetir.
- **Yapı / Roller:**
  - Originator: `TextEditor`
  - Memento: (editör durum snapshot’ı)
  - Caretaker: `EditorHistory`
  - Client: `MementoPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.memento`
  - Öne çıkan sınıflar: `TextEditor`, `EditorHistory`
- **Akış (adım adım):**
  1. Değişiklik öncesi snapshot alınır.
  2. Snapshot geçmişe itilir.
  3. Undo’da son snapshot geri yüklenir.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Geri alma/versiyonlama gerektiğinde.
  - Kullanılmaz: Durum çok büyükse ve hafıza kritikse.
- **Artılar / Eksiler:**
  - Artı: Kapsülleme bozulmadan rollback.
  - Eksi: Çok snapshot bellek tüketir.
- **Kısa gerçek hayat analojisi:** Oyundaki "save point" sistemi.

## C.6 Observer
**Derin okuma:** [`observer.md`](src/main/java/com/can/behavirol/observer/explain/observer.md)

- **Amaç (Intent):** Bir nesnedeki değişimi birden fazla aboneye otomatik iletmek.
- **Problem:** Yayıncı tüm dinleyicileri tek tek bilirse sıkı bağlılık oluşur.
- **Çözüm:** `Publisher` abonelik listesi yönetir; `Subscriber` implementasyonları bildirim alır.
- **Yapı / Roller:**
  - Subject: `Publisher`, `Store`
  - Observer: `Subscriber`
  - ConcreteObserver: `Customer`
  - Client: `ObserverPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.observer`
  - Öne çıkan sınıflar: `Publisher`, `Subscriber`, `Store`
- **Akış (adım adım):**
  1. Gözlemci subscribe olur.
  2. Subject durum değiştirir.
  3. Subject tüm abonelere notify eder.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Event-driven bildirim akışında.
  - Kullanılmaz: Alıcı sayısı sabit ve çok azsa.
- **Artılar / Eksiler:**
  - Artı: Gevşek bağlı yayın-abone modeli.
  - Eksi: Bildirim sırası/yan etkiler izlenmelidir.
- **Kısa gerçek hayat analojisi:** YouTube kanalına abone olup yeni video bildirimi almak.

## C.7 State
**Derin okuma:** [`state.md`](src/main/java/com/can/behavirol/state/explain/state.md)

- **Amaç (Intent):** Nesnenin davranışını, mevcut durumuna göre değiştirmek.
- **Problem:** Çok sayıda `if/else` ile durum geçişleri karmaşıklaşır.
- **Çözüm:** Her durumu ayrı sınıf yapıp (`DraftState`, `ModerationState`, `PublishedState`) davranışı bu sınıflara dağıtmak.
- **Yapı / Roller:**
  - Context: `DocumentContext`
  - State: `DocumentState`
  - ConcreteState: `DraftState`, `ModerationState`, `PublishedState`
  - Client: `StatePatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.state`
  - Öne çıkan sınıflar: `DocumentContext`, `DocumentState`
- **Akış (adım adım):**
  1. Context mevcut state ile başlar.
  2. Eylem çağrısı state nesnesine delege edilir.
  3. State, gerekiyorsa bir sonraki state’e geçiş yapar.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Net yaşam döngüsü/durum makinesi varsa.
  - Kullanılmaz: Durum sayısı çok az ve statikse.
- **Artılar / Eksiler:**
  - Artı: Koşul karmaşası azalır.
  - Eksi: Durum sınıfı sayısı artar.
- **Kısa gerçek hayat analojisi:** Siparişin "hazırlanıyor-kargoda-teslim" aşamaları.

## C.8 Strategy
**Derin okuma:** [`strategy.md`](src/main/java/com/can/behavirol/strategy/explain/strategy.md)

- **Amaç (Intent):** Algoritmaları değiştirilebilir aileler halinde kapsüllemek.
- **Problem:** Hesaplama yöntemi değiştikçe tek sınıfta koşul yığını oluşur.
- **Çözüm:** `CalculationStrategy` arayüzü ve `Add/Multiply/Subtract` stratejileri; `CalculatorContext` çalışma anında seçim yapar.
- **Yapı / Roller:**
  - Strategy: `CalculationStrategy`
  - ConcreteStrategy: `AddStrategy`, `MultiplyStrategy`, `SubtractStrategy`
  - Context: `CalculatorContext`
  - Client: `StrategyPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.strategy`
  - Öne çıkan sınıflar: `CalculatorContext`, `CalculationStrategy`
- **Akış (adım adım):**
  1. Context’e strateji enjekte edilir.
  2. İlgili işlem çağrısı yapılır.
  3. Context işi seçilen stratejiye delege eder.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Aynı işin birden fazla algoritması varsa.
  - Kullanılmaz: Algoritma hiç değişmiyorsa.
- **Artılar / Eksiler:**
  - Artı: Çalışma anında algoritma değişimi.
  - Eksi: İstemci doğru stratejiyi seçmelidir.
- **Kısa gerçek hayat analojisi:** Navigasyonda "en kısa", "en hızlı", "ücretli yoldan kaçın" rotaları.

## C.9 Template Method
**Derin okuma:** [`templatemethod.md`](src/main/java/com/can/behavirol/templatemethod/explain/templatemethod.md)

- **Amaç (Intent):** İş akışının iskeletini sabitleyip bazı adımları alt sınıflara bırakmak.
- **Problem:** Benzer süreçlerde tekrar eden adımlar kopyalanır.
- **Çözüm:** `DocumentMiningTemplate` akış iskeletini tutar; `PdfDocumentMiner`, `CsvDocumentMiner` değişken adımları doldurur.
- **Yapı / Roller:**
  - AbstractClass: `DocumentMiningTemplate`
  - ConcreteClass: `PdfDocumentMiner`, `CsvDocumentMiner`
  - Client: `TemplateMethodPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.templatemethod`
  - Öne çıkan sınıflar: `DocumentMiningTemplate`
- **Akış (adım adım):**
  1. Şablon metot çağrılır.
  2. Ortak adımlar sabit sırayla çalışır.
  3. Değişken adımlar alt sınıf implementasyonunu kullanır.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Süreç iskeleti aynı, adım detayı farklıysa.
  - Kullanılmaz: Adım sırası/iskelet de değişkense.
- **Artılar / Eksiler:**
  - Artı: Tekrarlı akış kodunu azaltır.
  - Eksi: Kalıtıma bağımlılığı artırır.
- **Kısa gerçek hayat analojisi:** Kahve makinesinde aynı temel demleme akışı, farklı çekirdek profili.

## C.10 Visitor
**Derin okuma:** [`visitor.md`](src/main/java/com/can/behavirol/visitor/explain/visitor.md)

- **Amaç (Intent):** Nesne yapısını değiştirmeden yeni operasyonlar eklemek.
- **Problem:** Aynı nesne yapısına sürekli yeni analiz/çıktı işlemleri eklenir.
- **Çözüm:** `GeoNodeVisitor` ile operasyon ayrıştırılır; `XmlExportVisitor`, `RiskAuditVisitor` gibi yeni davranışlar eklenir.
- **Yapı / Roller:**
  - Visitor: `GeoNodeVisitor`
  - ConcreteVisitor: `XmlExportVisitor`, `RiskAuditVisitor`
  - Element: `GeoNode`
  - ConcreteElement: `City`, `Industry`, `SightSeeing`
  - Client: `VisitorPatternDemo`
- **Projedeki karşılığı (paket ve sınıf adları):**
  - Paket: `com.can.behavirol.visitor`
  - Öne çıkan sınıflar: `GeoNode`, `GeoNodeVisitor`
- **Akış (adım adım):**
  1. Visitor nesnesi oluşturulur.
  2. Her element `accept(visitor)` çağırır.
  3. Double-dispatch ile doğru ziyaret metodu çalışır.
- **Ne zaman kullanılır / kullanılmaz:**
  - Kullanılır: Element yapısı stabil, operasyonlar sık değişiyorsa.
  - Kullanılmaz: Element türleri sık ekleniyorsa.
- **Artılar / Eksiler:**
  - Artı: Yeni operasyon eklemek kolay.
  - Eksi: Yeni element eklemek tüm visitor’ları etkiler.
- **Kısa gerçek hayat analojisi:** Müze rehberinin her eser türü için farklı anlatım yapması.

---

## 3) Pattern Seçme Rehberi

### Problem tipine göre öneri tablosu

| Problem tipi | Önerilen pattern(ler) | Kısa not |
|---|---|---|
| Nesne üretimi karmaşık / çok parametreli | **Builder**, Factory Method | Adım adım güvenli kurulum |
| Üretilecek sınıf çalışma anında belli oluyor | **Factory Method** | Somut sınıf bağımlılığını azaltır |
| Birbiriyle uyumlu ürün aileleri üretilecek | **Abstract Factory** | Tema/aile tutarlılığı sağlar |
| Hazır nesneden hızlı çoğaltma gerekiyor | **Prototype** | Kopyalama maliyeti düşürür |
| Uygulamada tek paylaşılan örnek gerek | **Singleton** | Global erişim + tek örnek |
| Uyuşmayan iki arayüz entegre edilecek | **Adapter** | Eski-yeni sistemi bağlar |
| İki boyutlu varyasyon var (A x B) | **Bridge** | Sınıf patlamasını önler |
| Sıralı doğrulama/filtre hattı kurulacak | **Chain of Responsibility** | Adım adım işlem zinciri |
| İşlemler kuyruklanacak/undo yapılacak | **Command**, Memento | Komut geçmişi ve geri alma |
| Veri dolaşımı iç yapıyı açmadan yapılacak | **Iterator** | Standart gezinme API’si |
| Çoklu bileşen koordinasyonu var | **Mediator** | Merkezileştirilmiş iletişim |
| Duruma göre davranış değişiyor | **State** | if/else yükünü azaltır |
| Aynı iş için alternatif algoritmalar var | **Strategy** | Runtime algoritma seçimi |
| Süreç iskeleti aynı, adımlar değişken | **Template Method** | Tekrarlanan akışları toplar |
| Veri yapısı sabit, operasyonlar sık değişiyor | **Visitor** | Yeni operasyon eklemesi kolay |
| Olay olduğunda çoklu aboneye bildirim | **Observer** | Yayın-abone modeli |

### Hızlı seçim notu
- Önce **problemi** adlandırın (üretim mi, yapı mı, davranış mı?).
- Sonra **değişken olanı** bulun (nesne tipi mi, algoritma mı, durum mu?).
- En düşük karmaşıklıkla çözen pattern’i seçin; gereksiz soyutlamadan kaçının.
