# Command Pattern (Komut Deseni)

**Diğer adları:** Action, Transaction  
**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Command pattern, bir isteği (request) bağımsız bir nesneye dönüştürür. Bu nesne çağrının tamamını taşır:

- Hangi alıcı (receiver) nesne çağrılacak?
- Hangi metot çalışacak?
- Hangi veriler/parametreler kullanılacak?

Böylece istekler:

- Parametre olarak taşınabilir,
- Kuyruğa alınabilir veya geciktirilebilir,
- Loglanabilir,
- Undo/redo mekanizmasına dahil edilebilir.

---

## Problem (Neden ihtiyaç var?)

Bir editörde toolbar, context menu ve kısayollarla aynı işi tetiklediğini düşün:

- `Ctrl+C`
- Sağ tık menüsündeki **Copy**
- Üst bardaki **Copy** butonu

Eğer her UI elemanı işi doğrudan kendisi yaparsa:

- Aynı iş kuralı birden fazla yere kopyalanır.
- UI katmanı, business katmanına sıkı bağlanır.
- Yeni tetikleme kanalı eklemek (hotkey, API, macro) zorlaşır.

Command, bu bağı koparıp isteği tek bir komut nesnesinde toplar.

---

## Çözüm (Pattern nasıl çözüyor?)

### Temel roller

- **Invoker (Sender):** Komutu tetikleyen taraf (buton, toolbar, scheduler)
- **Command interface:** Her komut için ortak sözleşme (`execute`)
- **Concrete Command:** Belirli işi yapan komut sınıfı
- **Receiver:** Gerçek işi yapan domain/business nesnesi
- **Client:** Komutları oluşturup invoker’a bağlayan kurucu katman

### Kritik fikir

Invoker, "hangi iş nasıl yapılacak" bilgisini bilmez. Sadece komutu çağırır.

Bu, UI ile business logic arasında güçlü bir ara katman oluşturur.

---

## OOP Örneği (Projeden)

Bu projede Command pattern, mini bir editör senaryosu ile uygulanmıştır.

### Akış

1. `EditorToolbar` buton isimleriyle `Command` nesnelerini eşler.
2. Buton tıklanınca `Command#execute()` çağrılır.
3. State değiştiren komutlar `CommandHistory` içine alınır.
4. `UndoCommand` çağrıldığında son komut geri alınır.

### Sınıfların pattern rolleri

- **Command interface:** `Command`
- **Concrete Commands:** `WriteTextCommand`, `CopyCommand`, `PasteCommand`, `UndoCommand`
- **Receiver:** `Editor` (metin yazma/değiştirme işlemleri)
- **Invoker:** `EditorToolbar`
- **History:** `CommandHistory`
- **Client/Demo:** `CommandPatternDemo`

---

## Derin Teknik Açıklama

### 1) Neden `execute()` parametresiz?

Invoker'ın komutu tek tip çağırabilmesi için.
Gerekli veriler komut nesnesine constructor ile önceden verilir.

Bu sayede runtime'da farklı komutlar aynı noktadan yönetilir.

### 2) Undo neden command history ile kolaylaşıyor?

State değiştiren komutlar, çalışmadan önce `backup` alır.
Sonra history stack'e itilir.

Undo sırasında:

- Son komut stack’ten çekilir.
- Kendi `undo()` metodu çalışır.

Invoker, komutun iç detayıyla ilgilenmez.

### 3) Neden SRP/OCP güçleniyor?

- Yeni bir özellik için mevcut buton sınıflarını değiştirmezsin.
- Sadece yeni command sınıfı eklersin.
- Invoker hep aynı kalır.

Bu doğrudan **Open/Closed Principle** avantajıdır.

---

## Gerçek Dünya Analojisi

Restorandaki sipariş fişi tam bir Command örneğidir:

- Müşteri isteği verir.
- Garson isteği fişe yazar (komut nesnesi).
- Mutfak fişe göre üretim yapar (receiver).
- Fiş kuyruğa girebilir, ertelenebilir, takip edilebilir.

Garson aşçıya doğrudan "nasıl pişireceğini" anlatmak zorunda kalmaz.

---

## Ne Zaman Kullanmalı?

- UI event’leri ile domain logic’i gevşek bağlamak istediğinde,
- İşlemleri kuyruklamak/zamanlamak istediğinde,
- Undo/redo gerektiğinde,
- Loglanabilir/audit edilebilir işlem akışı kurmak istediğinde.

---

## Artılar / Eksiler

### Artılar

- Gönderen ve alan arasında gevşek bağlılık
- Undo/redo, queue, log altyapısına doğal uyum
- Yeni komut eklemek kolay (OCP)

### Eksiler

- Sınıf sayısını artırır
- Basit senaryolarda fazla soyutlama maliyeti olabilir

---

## İlişkili Pattern’ler

- **Memento:** Undo için state saklama modelini güçlendirir.
- **Strategy:** İkisi de davranışı nesneye taşır; Strategy algoritma seçer, Command çağrıyı nesneleştirir.
- **Chain of Responsibility:** Handler’lar command olarak modellenebilir.
- **Mediator/Observer:** İletişim modelini farklı açılardan düzenler.
