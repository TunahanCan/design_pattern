# Memento Pattern (Anlık Görüntü / Snapshot)

**Diğer adı:** Snapshot  
**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Memento pattern, bir nesnenin önceki durumunu kaydedip gerektiğinde geri yüklemeyi sağlar.
Bunu yaparken nesnenin private alanlarını dışarı açmaz; yani **encapsulation bozulmaz**.

---

## Problem

Undo/geri alma gibi özelliklerde tipik ihtiyaç şudur:

- İşlemden önce nesnenin durumunu sakla,
- Geri al denince son güvenli duruma dön.

Dışarıdan bir sınıfın nesnenin tüm alanlarını okuyup kopyalaması iki probleme yol açar:

1. Private verilere erişmek gerekir (kapsülleme bozulur),
2. Nesne yapısı değiştiğinde kopyalama kodları kırılır.

---

## Çözüm

Memento deseni sorumluluğu doğru yere taşır:

- **Originator** (durumu tutan asıl nesne), kendi snapshot’ını kendi üretir.
- **Memento**, bu snapshot verisini immutable şekilde saklar.
- **Caretaker**, sadece memento’ları saklar (stack/history), içeriklerini değiştirmez.

Böylece geçmiş tutulur ama iç detaylar korunur.

---

## Projedeki OOP Örneği

Bu projede Memento akışı bir text editor senaryosu ile modellendi:

- `TextEditor` → **Originator**
- `TextEditor.Snapshot` → **Memento**
- `EditorHistory` → **Caretaker**
- `MementoPatternDemo` → örnek çalışma akışı

### Akış

1. `TextEditor`, ilk durumunu `createSnapshot(...)` ile üretir.
2. `EditorHistory`, snapshot’ları stack olarak saklar.
3. Editörde değişiklikler yapılır ve her önemli adımda yeni snapshot eklenir.
4. Undo anında history’den son uygun snapshot çekilir ve `editor.restore(...)` ile durum geri yüklenir.

---

## Neden bu örnek Memento?

Çünkü geçmişe dönmek için editörün iç alanları (`text`, `cursor`, `selectionWidth`) dış sınıflara açılmıyor.

- Snapshot’ı yalnızca editör üretip geri okuyabiliyor.
- Caretaker sadece “hangi adım saklandı?” bilgisini kullanıyor.
- Undo özelliği kapsüllemeyi bozmadan çalışıyor.

Bu, pattern’in temel fikrini birebir karşılar.

---

## Artılar / Eksiler

### Artılar

- Encapsulation korunur.
- Undo/rollback akışı temiz şekilde kurgulanır.
- Originator kodu sadeleşir; geçmiş yönetimi caretaker’a taşınır.

### Eksiler

- Çok sık snapshot almak bellek tüketimini artırabilir.
- Caretaker geçmişin yaşam döngüsünü iyi yönetmelidir.

---

## Ne zaman kullanılmalı?

- Undo/redo ihtiyacı varsa,
- Hata durumunda rollback gerekiyorsa,
- Nesnenin iç yapısını dışarı açmadan geçmiş saklamak istiyorsan,

Memento pattern doğru tercihtir.
