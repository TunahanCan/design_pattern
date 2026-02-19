# Template Method Pattern (Şablon Metot Deseni)

**Tür:** Behavioral (Davranışsal)

## Intent (Amaç)

Template Method, bir algoritmanın **iskeletini** üst sınıfta tanımlar; alt sınıflar ise bu algoritmanın belirli adımlarını özelleştirir.

Yani algoritmanın akış sırası sabit kalır, değişmesi gereken detay adımlar alt sınıflara bırakılır.

---

## Problem

Veri madenciliği yapan bir uygulamada farklı dosya tipleri (PDF, CSV, DOC) işlenirken genelde benzer bir akış oluşur:

1. Dosyayı aç
2. Ham veriyi çıkar
3. Veriyi analiz et
4. Rapor üret
5. Dosyayı kapat

Formatlara göre bazı adımlar farklı olsa da algoritmanın ana akışı aynıdır. Bu durum tek tek sınıflarda tekrar eden kodlara ve istemci tarafında `if/switch` karmaşasına yol açar.

---

## Çözüm

Template Method yaklaşımında:

- Ortak akış, abstract bir üst sınıfta `template method` içinde toplanır.
- Değişken adımlar abstract metot olarak alt sınıflara zorunlu bırakılır.
- Bazı adımlar için üst sınıfta varsayılan davranış verilebilir.
- İstenirse **hook** metotlar ile (boş gövdeli opsiyonel adımlar) genişletme noktaları açılır.

Böylece kod tekrarı azalır ve algoritma bütünlüğü bozulmadan özelleştirme yapılır.

---

## Projedeki OOP Örneği

Bu projede Template Method örneği, doküman madenciliği senaryosu ile uygulanmıştır:

- `DocumentMiningTemplate`:
  - `process(fileName)` adlı **final template method** ile akışı sabitler.
  - `openFile`, `extractRawData`, `closeFile` adımlarını abstract bırakır.
  - `analyzeData`, `createReport` için varsayılan implementasyon sunar.
  - `beforeAnalyze`, `afterReport` hook metotları sağlar.
- `PdfDocumentMiner`:
  - PDF'e özgü açma/çıkarma/kapama adımlarını implement eder.
  - `beforeAnalyze` hook'unu OCR kontrolü için override eder.
- `CsvDocumentMiner`:
  - CSV'e özgü adımları implement eder.
  - `analyzeData` ve `createReport` adımlarını özel kuralla override eder.
  - `afterReport` hook'u ile rapor sonrası aksiyon ekler.
- `TemplateMethodPatternDemo`:
  - Aynı `process(...)` çağrısını farklı alt sınıflarla çalıştırarak polymorphism'i gösterir.

---

## Akışın Nasıl Çalıştığı

`process(...)` metodunun sırası her zaman aynıdır:

1. `openFile`
2. `extractRawData`
3. `beforeAnalyze` (hook)
4. `analyzeData`
5. `createReport`
6. `afterReport` (hook)
7. `closeFile`

Bu sırayı alt sınıflar değiştiremez; sadece ilgili adımların içeriğini özelleştirir.

---

## Ne Zaman Kullanılır?

- Sınıflarda algoritma akışı aynı, bazı adımlar farklıysa,
- Kopya kodlar artmaya başladıysa,
- İstemci tarafında sınıf türüne göre çok sayıda koşul yazılıyorsa,
- Akışın bütünlüğünü koruyup yalnızca belirli adımları genişletmek istiyorsanız.

---

## Avantajlar / Dezavantajlar

### Avantajlar

- Kod tekrarı azalır.
- Algoritma akışı tek yerde standardize edilir.
- Alt sınıflara kontrollü genişletme imkanı verir.

### Dezavantajlar

- Kalıtım hiyerarşisi büyüyebilir.
- Çok fazla adımı olan template method zamanla karmaşıklaşabilir.
- Yanlış override'lar LSP benzeri tasarım problemlerine yol açabilir.

---

## Strategy ile Farkı (Kısa)

- **Template Method:** Kalıtım tabanlıdır; algoritma yapısı sınıf seviyesinde sabittir.
- **Strategy:** Kompozisyon tabanlıdır; davranış nesne seviyesinde runtime'da değiştirilir.

---

## Bu Projeden Bakarak Özet

Bu örnekte iş akışı `DocumentMiningTemplate` içinde merkezi olarak korunur. PDF ve CSV sınıfları sadece farklı olan parçaları değiştirir. Böylece hem OOP kalıtım/polimorfizm kullanılır hem de algoritmanın omurgası tek noktada güvenli kalır.
