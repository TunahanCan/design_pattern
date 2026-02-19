# Chain of Responsibility (Sorumluluk Zinciri)

**Diğer adları:** CoR, Chain of Command  
**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Chain of Responsibility, bir isteğin birden fazla işleyici (handler) arasında zincir şeklinde gezmesini sağlar. Her işleyici isteği alınca iki karardan birini verir:

1. İsteği kendi başına işler.
2. İsteği zincirdeki bir sonraki işleyiciye iletir.

Böylece istemci kodu, isteğin tam olarak hangi sınıf tarafından işleneceğine bağımlı kalmaz.

---

## Problem

Bir online sipariş sisteminde şu kontrollerin sırayla yapılması gerektiğini düşün:

- Kullanıcı kimliği doğrulanmış mı?
- Veri güvenli mi (sanitize edildi mi)?
- Brute-force saldırısı şüphesi var mı?
- Aynı isteğin cache sonucu var mı?
- İşlem için yetki yeterli mi? (ör. tüm siparişleri görme)

Bu kontroller tek bir sınıfta `if-else` ile büyüdükçe:

- Kod şişer ve karmaşıklaşır.
- Bir kontrolü değiştirmek diğerini etkileyebilir.
- Başka akışlarda tekrar kullanım zorlaşır.

---

## Çözüm

Her kontrolü ayrı bir `Handler` sınıfına çıkarırız. Sonra bu handler'ları zincir halinde birbirine bağlarız.

- Her handler yalnızca kendi sorumluluğunu bilir.
- Gerekirse isteği durdurur (zinciri kırar).
- Gerekirse bir sonrakine geçirir.

Bu projedeki örnekte akış:

1. `BruteForceProtectionHandler`
2. `AuthenticationHandler`
3. `DataSanitizationHandler`
4. `AuthorizationHandler`
5. `CacheHandler`
6. `OrderProcessingHandler`

`ChainOfResponsibilityDemo#buildChain` içinde bu sıra runtime'da kuruluyor.

---

## Yapı (Structure)

- `OrderRequestHandler`: Tüm handler'ların ortak arayüzü.
- `BaseOrderRequestHandler`: Sonraki handler referansını ve varsayılan iletme davranışını tutan temel sınıf.
- Concrete handler'lar: Her biri tek bir iş kuralı uygular.
- `OrderRequest`: Zincirde taşınan istek nesnesi (context).
- `ChainOfResponsibilityDemo`: Zinciri kuran ve örnek istekleri çalıştıran istemci.

---

## OOP Örneği (Projeden)

Bu projede örnek bir sipariş güvenlik hattı kuruldu:

- Kimlik doğrulama başarısızsa zincir hemen durur.
- Admin olmayan kullanıcı `VIEW_ALL_ORDERS` işlemini yapamaz.
- Zararlı payload temizlenir.
- Aynı imzaya sahip istek tekrar gelirse cache devreye girip işleme gitmeden akış sonlandırılır.

İlgili sınıflar:

- `src/main/java/com/can/behavirol/chainofresponsibility/ChainOfResponsibilityDemo.java`
- `src/main/java/com/can/behavirol/chainofresponsibility/*Handler.java`
- `src/main/java/com/can/behavirol/chainofresponsibility/OrderRequest.java`

---

## Gerçek Dünya Analojisi

Teknik destek hattını düşün:

- Önce otomatik sesli yanıt sistemi,
- Sonra çağrı merkezi operatörü,
- Sonra uzman mühendis.

Her adım sorunu çözemezse bir sonraki seviyeye aktarır. İşte bu tam olarak bir sorumluluk zinciridir.

---

## Ne Zaman Kullanılır?

- İsteklerin farklı türlerde ve farklı sıralarda işlenmesi gerekiyorsa.
- İşleyicilerin sırası önemliyse.
- İşleyici seti runtime'da değişebilecekse.

---

## Artılar / Eksiler

### Artılar

- İşlem sırasını net yönetirsin.
- `SRP`: Her handler tek sorumluluk taşır.
- `OCP`: Yeni handler eklerken mevcut istemci kodu bozulmaz.

### Eksiler

- Bazı istekler zincirin sonunda hiç işlenmeden kalabilir.
- Çok uzun zincirlerde akışı takip etmek zorlaşabilir.

---

## İlişkili Pattern'ler

- **Composite** ile birlikte sık görülür: Ağaç yapısındaki üst bileşenlere doğru istek yükselir.
- **Decorator** ile yapısal benzerlik taşır ama CoR'da akış kesilebilir; Decorator'da genelde kesilmez.
- **Command / Mediator / Observer** ile birlikte gönderici-alıcı bağlantısını farklı şekillerde ele alır.

