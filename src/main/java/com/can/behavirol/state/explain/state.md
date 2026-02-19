# State Pattern (Durum Deseni)

**Tür:** Behavioral (Davranışsal)

## Intent (Amaç)

State, bir nesnenin iç durumu değiştiğinde davranışının da değişmesini sağlar. Dışarıdan bakıldığında sanki nesnenin sınıfı değişmiş gibi görünür.

---

## Problem

Duruma göre farklı davranan bir sınıf düşünelim: `Document`.

- `Draft` durumunda `publish()` çağrısı dokümanı moderasyona taşır.
- `Moderation` durumunda sadece admin kullanıcı gerçekten yayınlayabilir.
- `Published` durumunda `publish()` artık bir şey yapmaz.

Bu yaklaşımı tek sınıf içinde `if/switch` ile yazdıkça:

- koşul blokları büyür,
- her yeni durum eski metotlara dokunmayı zorunlu kılar,
- bakım maliyeti artar.

---

## Çözüm

State deseni ile her durumu ayrı bir sınıfa taşırız:

- `DraftState`
- `ModerationState`
- `PublishedState`

`DocumentContext` aktif durumu bir referans olarak tutar ve işlemleri o state nesnesine delege eder.
Durum geçişleri state sınıfları içinden yapılır (`context.changeState(...)`).

---

## Projedeki OOP Örneği

Bu projedeki örnek yapı:

- `DocumentContext` → **Context**, aktif state'i tutar
- `DocumentState` → ortak davranış arayüzü (`publish`, `edit`, `getName`)
- `DraftState`, `ModerationState`, `PublishedState` → **Concrete State** sınıfları
- `StatePatternDemo` → örnek senaryo

### Senaryo Akışı

1. Doküman `Draft` olarak başlar.
2. `publish()` çağrısı ile `Moderation` durumuna geçer.
3. Editör rolüyle `publish()` denenirse yayınlanmaz.
4. Rol `admin` olunca aynı çağrı dokümanı `Published` durumuna geçirir.
5. `Published` durumunda düzenleme/yayınlama çağrıları artık farklı (no-op / bilgilendirme) davranır.

Bu sayede durum bazlı davranışlar tek sınıfta koşul yığınına dönüşmez; her durum kendi sorumluluğunu taşır.

---

## Neden faydalı?

- **SRP:** Her state sınıfı kendi davranışından sorumlu.
- **OCP:** Yeni durum eklemek için mevcut state'leri bozmak gerekmez.
- Context sınıfı sade kalır, karmaşık koşullar azalır.

---

## State vs Strategy kısa fark

Yapı olarak benzer görünürler (composition + delegation).

- **Strategy**: Algoritma seçimi yapılır, stratejiler genelde birbirini bilmez.
- **State**: Nesnenin yaşam döngüsü / durum geçişi yönetilir, state'ler birbirini bilip geçiş başlatabilir.
