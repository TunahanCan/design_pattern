# Composite Pattern (Yapısal / Structural)

## Niyet (Intent)

Composite, nesneleri ağaç yapısında birleştirmeni sağlar. Böylece tek bir nesneyle çalışır gibi, karmaşık bir nesne grubuyla da aynı arayüz üzerinden çalışabilirsin.

Diğer adı: **Object Tree**.

---

## Problem

Modelin ağaç yapısındaysa (ürün-kutu-kutu içinde ürün gibi), istemci kodunun her seviyeyi ve her somut sınıfı ayrı ayrı bilmesi gerekir.

Örnek:
- `Product` (yaprak)
- `Box` (içine ürün veya başka kutu alabilen bileşik)

Toplam fiyat hesaplamak istediğinde kutuları tek tek açıp sınıf tiplerine göre ilerlemek karmaşıklaşır.

---

## Çözüm

Composite, tüm elemanlar için ortak bir arayüz önerir.

Bu projede bu arayüz:
- `OrderComponent`

Ortak operasyon:
- `getPrice()`

Nasıl çalışır?
- `Product#getPrice()` doğrudan ürün fiyatını döner.
- `Box#getPrice()` kendi içindeki tüm elemanların fiyatlarını **özyinelemeli (recursive)** şekilde toplar, ayrıca paketleme maliyetini ekler.

Böylece istemci, nesnenin ürün mü kutu mu olduğunu bilmeden aynı metodu çağırır.

---

## Projedeki OOP Örneği

### Component
- `OrderComponent`: `getPrice()` ve `getName()` metodlarını tanımlar.

### Leaf
- `Product`: alt elemanı yoktur, gerçek işi yapar (fiyat döndürür).

### Composite
- `Box`: içinde `List<OrderComponent>` tutar.
- `add/remove` ile çocuk yönetir.
- `getPrice()` içinde çocuklara delege ederek toplamı üretir.

### Client
- `CompositePatternDemo`: ürün ve iç içe kutular kurar, tek çağrıyla toplam sipariş fiyatını hesaplar.

---

## Neden Composite?

Çünkü istemci tarafı şunu bilmek zorunda kalmaz:
- Bu eleman tek ürün mü?
- Yoksa içinde başka elemanlar olan bir kutu mu?

Her ikisi de aynı arayüzü uygular. Bu da polymorphism + recursion ile sade bir kullanım sağlar.

---

## Uygulanabilirlik (Applicability)

Composite kullan:
- Modelin doğal olarak ağaç yapısındaysa,
- Basit ve karmaşık nesneleri istemcide **uniform** yönetmek istiyorsan.

---

## Artılar / Eksiler

### Artılar
- Karmaşık ağaç yapılarında kullanım kolaylığı.
- Open/Closed: yeni yaprak veya yeni composite tipleri kolay eklenir.
- İstemci kodu sadeleşir.

### Eksiler
- Ortak arayüz, bazı sınıflar için fazla genel kalabilir.
- Yanlış modellemede gereksiz soyutlama yaratabilir.

---

## Kısa Özet

Composite, `Product` ve `Box` gibi farklı seviyedeki nesneleri tek bir `OrderComponent` arayüzünde toplar. Bu projede toplam fiyat hesabı, ağaç yapıda recursive dolaşım ile yapılır ve istemci her düğümü aynı şekilde kullanır.
