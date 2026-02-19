# Iterator Pattern (Yineleyici Deseni)

**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Iterator pattern, bir koleksiyonun iç yapısını (liste, ağaç, stack vb.) açığa çıkarmadan elemanlarını sırayla gezmeyi sağlar.

Yani client tarafı şunu bilmez:
- Veri içeride nasıl tutuluyor?
- Gezinme algoritması nasıl çalışıyor?

Client sadece iterator arayüzündeki metotları kullanır: `hasMore()` ve `getNext()`.

---

## Projedeki OOP Örneği

Bu projede örnek, sosyal ağ profilleri üzerinden kurgulandı:

- `SocialNetwork` koleksiyon rolünde davranır.
- `Facebook` somut koleksiyondur.
- `ProfileIterator` ortak iterator arayüzüdür.
- `SocialGraphIterator` somut iterator’dır.
- `SocialSpammer` client rolündedir; sadece iterator alır ve mail gönderir.

### Akış

1. `IteratorPatternDemo`, `SocialGraph` içine profil verilerini koyar.
2. Client, `network.createFriendsIterator("1")` veya `createCoworkersIterator("1")` çağırır.
3. `SocialSpammer`, iterator üzerinden profile profile dolaşıp mesaj yollar.
4. Aynı koleksiyon için farklı iterator türleri kullanılabilir.

Bu sayede koleksiyon sınıfına traversal kodu yığılmaz, client da veri yapısına bağımlı olmaz.

---

## Nasıl Çalışıyor?

`SocialGraphIterator` içinde iki kritik nokta vardır:

- **Durum tutma (state):** `currentPosition` alanı ile iterator nerede kaldığını bilir.
- **Lazy init:** `cache` ilk erişimde doldurulur. Böylece gereksiz veri çekimi olmaz.

Her yeni iterator çağrısında yeni bir nesne üretildiği için:
- Aynı koleksiyon paralel olarak farklı yerlerden gezilebilir.
- İki iterator birbirinin durumunu bozmaz.

---

## Neden Faydalı?

- **SRP:** Gezinme mantığı ayrı sınıfta.
- **OCP:** Yeni gezinme türü eklemek için yeni iterator yazmak yeterli.
- **Loose coupling:** Client, somut koleksiyon detayını bilmeden çalışır.

---

## Ne Zaman Kullanılır?

- Koleksiyon karmaşık bir veri yapısıysa,
- Birden fazla gezinme stratejisine ihtiyaç varsa,
- Client tarafını koleksiyon iç detaylarından izole etmek istiyorsan.

---

## Kısa Özet

Iterator pattern, “koleksiyon nasıl tutuluyor?” sorusunu gizleyip “sıradaki eleman nedir?” sorusuna odaklanır. Bu da hem daha temiz OOP tasarımı hem de daha esnek traversal sağlar.
