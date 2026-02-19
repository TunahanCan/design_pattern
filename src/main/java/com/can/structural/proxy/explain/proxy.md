# Proxy Pattern (Yapısal / Structural)

## Intent

**Proxy**, başka bir nesnenin yerine geçen bir temsilci (substitute/placeholder) sağlar.
Bu temsilci, isteğin gerçek nesneye gitmeden önce veya gittikten sonra ek işlemler yaparak erişimi kontrol eder.

Kısaca:
- Arayüz aynı kalır,
- İstemci değişmez,
- Erişim kontrolü / cache / log / lazy initialization gibi ihtiyaçlar proxy katmanında çözülür.

---

## Problem

Neden bir nesneye erişimi kontrol etmek isteriz?

Çünkü bazı servis nesneleri:
- Ağırdır (çok kaynak tüketir),
- Uzak kaynaktan veri çeker (gecikme yaratır),
- Tekrarlı çağrılarda gereksiz maliyet üretir.

Örneğin YouTube benzeri bir serviste aynı videonun bilgisini her seferinde yeniden indirmek uygulamayı yavaşlatır.

---

## Solution

Proxy yaklaşımı şunu söyler:
1. Gerçek servisle **aynı interface**'e sahip bir Proxy sınıfı oluştur.
2. İstemcilerde gerçek servis yerine proxy ver.
3. Proxy, gerekirse gerçek servisi oluşturup çağrıyı ona delege etsin.

Böylece:
- Lazy initialization yapılabilir,
- Sonuçlar cache'lenebilir,
- Loglama ve access control eklenebilir,
- Tüm bunlar gerçek servisi değiştirmeden uygulanır.

---

## Projedeki OOP Örneği (YouTube Caching Proxy)

Bu projede aşağıdaki roller var:

- **Service Interface**: `ThirdPartyYouTubeLib`
- **Real Service**: `ThirdPartyYouTubeClass`
- **Proxy**: `CachedYouTubeClass`
- **Client**: `YouTubeManager`

### Nasıl çalışıyor?

- `ThirdPartyYouTubeClass`, video listesini, video bilgisini ve indirme çıktısını üretir.
- `CachedYouTubeClass`, aynı çağrıları cache üzerinden yönetir:
  - `listVideos()` sonucu tek sefer çağrılır.
  - `getVideoInfo(id)` aynı `id` için cache'den döner.
  - `downloadVideo(id)` aynı `id` için tekrar gerçek servise gitmez.
- `YouTubeManager`, servisle sadece interface üzerinden konuşur. Bu nedenle gerçek servis yerine proxy verilmesi istemciyi bozmaz.

---

## Real-World Analogy

Kredi kartı, nakit para için bir proxy gibi düşünülebilir.
Kullanıcı ödeme yapar ama fiziksel nakit taşımaz; kart, banka hesabı üzerinden aynı amaca ulaşır.

---

## Applicability

Proxy en çok şu durumlarda kullanılır:
- **Virtual Proxy (Lazy Initialization)**: Ağır nesneyi ihtiyaç anında oluşturmak.
- **Protection Proxy (Access Control)**: Sadece yetkili istemcileri geçirmek.
- **Remote Proxy**: Uzak servise ağ üzerinden erişimi soyutlamak.
- **Logging Proxy**: İstek geçmişini tutmak.
- **Caching Proxy**: Tekrarlı istekleri cache’den döndürmek.
- **Smart Reference**: Ağır nesnenin yaşam döngüsünü/aktif referanslarını yönetmek.

---

## Pros / Cons

### Artılar
- Gerçek servisi istemciden gizleyerek erişimi kontrol edebilirsin.
- Servis yaşam döngüsünü istemciden bağımsız yönetebilirsin.
- Open/Closed Principle: Yeni proxy'ler ekleyip davranış genişletilebilir.

### Eksiler
- Sınıf sayısı ve yapı karmaşıklığı artabilir.
- Proxy katmanı, çağrılara küçük bir gecikme ekleyebilir.

---

## Diğer Pattern'lerle İlişki

- **Adapter**: Farklı arayüzü uyumlu hale getirir. Proxy’de arayüz aynı kalır.
- **Decorator**: Davranışı zenginleştirir; Proxy çoğunlukla erişim/lifecycle yönetir.
- **Facade**: Karmaşık sistemi basit arayüzle sunar; Proxy ise gerçek servisle aynı arayüzü korur.

---

## Kısa Özet

Proxy, istemci ile gerçek servis arasına girip kontrol noktası oluşturan bir yapısal pattern’dir.
Bu projede `CachedYouTubeClass`, `ThirdPartyYouTubeClass` için cache odaklı bir proxy görevi görerek gereksiz tekrarlı çağrıları engeller.
