# Visitor Pattern (Ziyaretçi Deseni)

**Tür:** Behavioral (Davranışsal)

## Intent (Amaç)

Visitor, bir algoritmayı bu algoritmanın çalıştığı nesnelerden ayırmanızı sağlar.
Aynı nesne yapısı üzerinde farklı operasyonları (XML export, raporlama, validasyon, audit vb.) nesne sınıflarına dokunmadan eklemeye odaklanır.

---

## Problem

Büyük bir coğrafi grafik (graph) sistemi düşünün:

- `City` (şehir)
- `Industry` (endüstri)
- `SightSeeing` (turistik nokta)

Başta bu sınıflara birer `exportToXml()` metodu eklemek kolay görünür. Ama pratikte:

1. **Production riski**: Çalışan sınıfları değiştirmek yeni bug riski doğurur.
2. **Sorumluluk kirliliği**: Geodata sınıfının işi XML üretmek değildir.
3. **Sürekli değişim baskısı**: Yarın JSON export, ertesi gün audit, sonra farklı raporlar istenir.

Her yeni ihtiyaçta aynı sınıfları tekrar tekrar değiştirmek, bakım maliyetini yükseltir.

---

## Çözüm

Yeni davranışı nesnelerin içine gömmek yerine ayrı bir **Visitor** sınıfına taşı.

- Elemanlar (`City`, `Industry`, `SightSeeing`) sadece `accept(visitor)` sağlar.
- Visitor tarafında her somut tip için ayrı metot bulunur:
    - `visitCity(City city)`
    - `visitIndustry(Industry industry)`
    - `visitSightSeeing(SightSeeing sightSeeing)`

Böylece yeni davranış eklemek için yeni bir visitor sınıfı yazarsın.
Örneğin bu projede:

- `XmlExportVisitor` → XML satırları üretir.
- `RiskAuditVisitor` → risk notları üretir.

### Double Dispatch nasıl çalışır?

İstemci tek bir çağrı yapar:

```java
node.accept(visitor);
```

Ama gerçek metot seçimi iki adımda olur:

1. Runtime’da element tipi belirlenir (`City` / `Industry` / `SightSeeing`).
2. Element içindeki `accept`, doğru visitor metodunu çağırır (`visitCity(this)` gibi).

Bu sayede `if/else instanceof` zinciri yazmadan doğru metot çağrılır.

---

## Projedeki OOP Örneği

Paket: `com.can.behavirol.visitor`

- **Element interface**: `GeoNode`
- **Concrete elements**: `City`, `Industry`, `SightSeeing`
- **Visitor interface**: `GeoNodeVisitor`
- **Concrete visitors**: `XmlExportVisitor`, `RiskAuditVisitor`
- **Client/Demo**: `VisitorPatternDemo`

### Senaryo

1. Farklı tipte `GeoNode` nesnelerinden bir liste oluşturulur.
2. Aynı liste üzerinde iki farklı visitor çalıştırılır.
3. Sonuç olarak:
    - XML export alınır.
    - Ayrı bir risk audit raporu alınır.
4. Element sınıflarına yeni metot eklenmez; davranış visitor’larla gelir.

---

## Structure (Yapı)

1. **Visitor Interface**
   Her concrete element için bir `visitXxx(...)` metodu tanımlar.
2. **Concrete Visitor**
   Aynı operasyonun elemente göre özelleştirilmiş versiyonlarını uygular.
3. **Element Interface**
   `accept(Visitor v)` metodu içerir.
4. **Concrete Element**
   `accept` içinde kendine karşılık gelen visitor metodunu çağırır.
5. **Client**
   Element koleksiyonu üzerinde visitor’ı dolaştırır.

---

## Applicability (Ne zaman kullanılır?)

- Karmaşık bir nesne yapısındaki tüm elemanlara operasyon uygulamak istediğinde,
- Davranışları ana domain sınıflarından ayırmak istediğinde,
- Aynı eleman yapısına sık sık yeni operasyon ekleniyorsa,
- `instanceof` / `switch` ile tip kontrolü kalabalıklaşıyorsa.

---

## How to Implement (Adımlar)

1. Visitor arayüzünü tanımla; her concrete element için bir metot ekle.
2. Element arayüzüne `accept(Visitor)` ekle.
3. Tüm concrete elementlerde `accept` implement et ve uygun `visitXxx(this)` çağrısı yap.
4. Her yeni davranış için yeni concrete visitor yaz.
5. Client’ta element koleksiyonunu gezip `accept(visitor)` çağır.

---

## Pros / Cons

### Avantajlar

- **OCP uyumlu**: Yeni davranış eklemek için element sınıfları değişmez.
- **SRP iyileşir**: Yardımcı davranışlar ayrı sınıflara taşınır.
- Ziyaret sırasında visitor içinde durum biriktirilebilir (rapor, sayaç, istatistik).

### Dezavantajlar

- Yeni element tipi eklendiğinde tüm visitor’ların güncellenmesi gerekir.
- Visitor, elementin private verisine ihtiyaç duyarsa erişim/encapsulation gerilimi oluşabilir.

---

## Gerçek Hayat Analojisi

Tecrübeli bir sigorta acentesi mahalledeki binaları gezer:

- Konuta sağlık poliçesi,
- Bankaya hırsızlık poliçesi,
- Kafeye yangın/sel poliçesi önerir.

Aynı “ziyaret” operasyonu, farklı bina tiplerinde farklı davranış üretir.

---

## Diğer Pattern’lerle İlişki

- **Visitor vs Command**: Command işlemi nesneleştirir; Visitor farklı tiplerdeki nesnelere operasyon uygular.
- **Visitor + Composite**: Ağaç yapısının tamamına operasyon uygulamak için güçlü kombinasyondur.
- **Visitor + Iterator**: Karmaşık yapıyı Iterator ile gezip Visitor ile işlemi uygularsın.
