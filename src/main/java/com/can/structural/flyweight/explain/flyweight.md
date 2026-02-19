# Flyweight Pattern (Yapısal / Structural)

Diğer adı: **Cache**

## Niyet (Intent)

Flyweight, çok sayıda benzer nesnenin RAM kullanımını düşürmek için ortak (paylaşılabilir) durumu tek yerde tutar.
Her nesnede tüm alanları kopyalamak yerine:
- **intrinsic state** (değişmeyen, ortak veri) paylaşılır,
- **extrinsic state** (bağlama göre değişen veri) dışarıdan verilir.

---

## Problem

Oyun içinde mermi, roket, şarapnel gibi çok fazla parçacık (particle) üretildiğinde her parçacığın içinde aynı sprite/renk bilgilerini tekrar tutmak RAM’i hızla tüketir.

Örneğin binlerce mermi nesnesinin hepsinde:
- aynı sprite,
- aynı renk
saklanıyorsa gereksiz bellek harcanır.

---

## Çözüm

Flyweight yaklaşımıyla sınıf durumu ikiye ayrılır:

- **Intrinsic state (içsel durum):** Nesneler arasında ortak ve değişmeyen kısım.
  - Örn: ağaç tür adı, renk, texture.
- **Extrinsic state (dışsal durum):** Her nesneye özel bağlam verisi.
  - Örn: x/y koordinatı.

Paylaşılan veri `Flyweight` nesnesinde tutulur. Değişen veriler ise metoda parametre olarak geçilir ya da `Context` nesnesinde saklanır.

---

## Projedeki OOP Örneği

Bu projede klasik **Forest / Tree / TreeType** örneği kullanıldı:

- **Flyweight:** `TreeType`
  - Sadece ortak veriyi tutar (`name`, `color`, `texture`).
  - Immutable olacak şekilde sadece constructor ile set edilir.
- **Flyweight Factory:** `TreeFactory`
  - `getTreeType(...)` ile havuzdan uygun flyweight’i döner.
  - Yoksa yeni üretir, varsa mevcut olanı paylaşır.
- **Context:** `Tree`
  - Her ağaç için farklı olan `x`, `y` bilgilerini tutar.
  - Ayrıca bir `TreeType` referansı taşır.
- **Client/Container:** `Forest`
  - Çok sayıda `Tree` üretir.
  - Aynı tür ağaçlar için aynı `TreeType` nesnesini tekrar kullanır.
- **Demo:** `FlyweightPatternDemo`
  - 15 ağaç context’i üretir ama sadece 3 farklı `TreeType` flyweight saklar.

---

## Explain

Bu yapıda asıl kazanç şuradan gelir:

- 15 ağacın her biri ayrı nesne (context) olmaya devam eder.
- Fakat pahalı ve tekrar eden veri (tür bilgisi, renk, texture) her ağaçta ayrı ayrı tutulmaz.
- Bunun yerine sadece 3 tane `TreeType` oluşturulur ve tüm ağaçlar bu nesneleri paylaşır.

Yani sistem şunu yapar:
1. `Forest.plantTree(...)` çağrılır.
2. `TreeFactory`, istenen intrinsic veriye uygun `TreeType` arar.
3. Bulursa aynı nesneyi döner; bulamazsa üretip havuza ekler.
4. `Tree` sadece `x,y + TreeType ref` saklar.

Sonuç:
- RAM kullanımı ciddi şekilde düşer.
- Özellikle çok büyük koleksiyonlarda (on binlerce/milyonlarca nesne) etki büyür.
- Bedeli: Kod karmaşıklığı artar ve extrinsic state yönetimi istemci tarafına kayar.

---

## Ne zaman kullanılmalı?

Flyweight’i şu durumlarda kullan:
- Çok sayıda benzer nesne üretiliyorsa,
- Uygulama RAM sınırına dayanıyorsa,
- Nesnelerde ayrıştırılabilir tekrar eden ortak durum varsa.

---

## Artılar / Eksiler

### Artılar
- Çok büyük bellek tasarrufu sağlar.
- Büyük koleksiyonlarda ölçeklenebilirliği artırır.

### Eksiler
- Tasarım karmaşıklığı artar.
- Extrinsic state’in doğru taşınması gerekir.
- Bazen CPU maliyeti artabilir (bağlamı her çağrıda taşıma nedeniyle).

---

## Kısa Özet

Flyweight, “çok sayıda benzer nesnenin ortak kısmını paylaş” fikridir.
Bu projedeki `Forest` örneğinde çok sayıda `Tree` context nesnesi, az sayıda `TreeType` flyweight nesnesini paylaşarak bellek optimizasyonu sağlar.
