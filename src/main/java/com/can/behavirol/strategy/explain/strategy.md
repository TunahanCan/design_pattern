# Strategy Pattern (Strateji Deseni)

**Tür:** Behavioral (Davranışsal)

## Intent (Amaç)

Strategy, bir algoritma ailesini ayrı sınıflara ayırmanıza ve bu sınıfları birbirinin yerine kullanılabilir (interchangeable) hale getirmenize yardımcı olur.

---

## Problem

Navigasyon uygulaması örneğinde tek bir sınıfın farklı rota algoritmalarını (`araba`, `yürüme`, `toplu taşıma`, `bisiklet`...) yönetmesi zamanla şu problemlere yol açar:

- Ana sınıf aşırı büyür (bloated class).
- Yeni bir algoritma eklemek eski çalışan kodu riske atar.
- Ekip aynı büyük sınıfta çalıştığı için merge conflict artar.

Yani bir sınıfta çok fazla `if/switch` ile algoritma seçimi yapmak bakım maliyetini yükseltir.

---

## Çözüm

Strategy deseni, değişken davranışları ayrı strateji sınıflarına taşır:

- Ortak bir `Strategy` arayüzü tanımlanır.
- Her algoritma bu arayüzü uygulayan ayrı bir `ConcreteStrategy` sınıfına çıkarılır.
- `Context`, aktif stratejiyi referans olarak tutar ve işi stratejiye delege eder.
- İstemci (client), runtime'da stratejiyi değiştirerek farklı algoritma çalıştırır.

Böylece `Context` sınıfı somut algoritmaları bilmeden çalışır.

---

## Projedeki OOP Örneği

Bu projede Strategy örneği, hesap makinesi işlemleri üzerinden modellenmiştir:

- `CalculationStrategy` → Ortak strateji arayüzü (`execute`)
- `AddStrategy`, `SubtractStrategy`, `MultiplyStrategy` → Somut stratejiler
- `CalculatorContext` → Aktif stratejiyi tutan context
- `StrategyPatternDemo` → Runtime'da strateji değiştirilen demo akışı

### Senaryo Akışı

1. `CalculatorContext`, başlangıçta `AddStrategy` ile oluşturulur.
2. Aynı `calculate(a, b)` çağrısı ile toplama yapılır.
3. `setStrategy(new SubtractStrategy())` ile algoritma runtime'da değiştirilir.
4. Aynı çağrı bu kez çıkarma yapar.
5. Strateji tekrar `MultiplyStrategy` yapılarak çarpma çalıştırılır.

Aynı context, farklı algoritmaları herhangi bir `if/switch` bloğu olmadan kullanır.

---

## Structure (Yapı)

1. **Context**: Bir Strategy referansı tutar ve yalnızca arayüz üzerinden çağrı yapar.
2. **Strategy Interface**: Tüm algoritmalar için ortak metodu tanımlar.
3. **Concrete Strategies**: Algoritmanın farklı varyasyonlarını uygular.
4. **Client**: İhtiyaca göre doğru stratejiyi seçip context'e verir.

---

## Applicability (Ne zaman kullanılır?)

- Aynı işin farklı algoritmaları varsa ve runtime'da değiştirilecekse,
- Büyük bir koşul yapısı (`if/switch`) algoritma seçimi yapıyorsa,
- Benzer sınıflar sadece davranış farkı nedeniyle çoğalıyorsa,
- İş kuralı ile algoritma detayı ayrıştırılmak isteniyorsa.

---

## How to Implement (Adımlar)

1. Sık değişen algoritmayı belirle.
2. Ortak Strategy arayüzünü tanımla.
3. Algoritmaları tek tek concrete strategy sınıflarına taşı.
4. Context içine strategy referansı ve setter ekle.
5. Client tarafında doğru strategy seçilip context'e verilsin.

---

## Pros / Cons

### Avantajlar

- Algoritmalar runtime'da değiştirilebilir.
- Algoritma detayları, kullanan koddan izole edilir.
- Composition sayesinde inheritance baskısı azalır.
- OCP uyumludur: Yeni strateji eklemek için context'i değiştirmek gerekmez.

### Dezavantajlar

- Az sayıda ve sabit algoritma varsa gereksiz karmaşıklık oluşturabilir.
- Client, stratejiler arasındaki farkları bilmelidir.
- Çok sayıda strateji sınıfı kod tabanını büyütebilir.

---

## Gerçek Hayat Analojisi

Havalimanına gitmek için otobüs, taksi veya bisiklet seçenekleri düşünün.
Hedef aynıdır; sadece hedefe gitme algoritması (stratejisi) değişir.
Koşula göre uygun strateji seçilir.

---

## Diğer Pattern'lerle İlişki

- **Strategy vs State**: Yapı benzer olsa da Strategy algoritma seçimine, State durum geçişine odaklanır.
- **Strategy vs Command**: Command işlemi nesneleştirir (queue/history gibi amaçlar), Strategy aynı işi yapmanın farklı yollarını sunar.
- **Strategy vs Template Method**: Template Method kalıtım tabanlı ve sınıf seviyesinde; Strategy composition tabanlı ve nesne seviyesinde runtime değiştirilebilir.
