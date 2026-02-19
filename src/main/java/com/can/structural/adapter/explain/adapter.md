# Adapter Pattern (Yapısal / Structural)

**Diğer adı:** Wrapper

## Niyet (Intent)

Adapter, birbiriyle **uyumsuz arayüzlere** sahip sınıfların birlikte çalışmasını sağlar.
Kısaca: "Var olan servisi bozmadan, istemcinin anlayacağı dile çevirir."

---

## Problem

Elimizde istemci kodu var ve belirli bir arayüz bekliyor.
Ama kullanmak istediğimiz servis (legacy veya 3rd-party) farklı bir arayüz sunuyor.

Örnek fikir:
- Uygulama XML ile çalışıyor,
- Harici analiz kütüphanesi JSON bekliyor.

Doğrudan bağlamaya çalışırsak tip/format uyuşmazlığı yüzünden entegrasyon kırılır.

---

## Çözüm

Araya bir **Adapter** sınıfı koyarız:

- İstemcinin beklediği arayüzü uygular,
- İçinde gerçek servisi (adaptee) tutar,
- Gelen çağrıları/formatı çevirip gerçek servise iletir.

Böylece:
- İstemci değişmez,
- 3rd-party/legacy servis de değiştirilmez,
- Sadece dönüşüm mantığı adapter içinde yaşar.

---

## Projedeki OOP Örneği (Round Hole / Square Peg)

Bu projede klasik örnek kullanıldı:

- `RoundHole` → yalnızca `RoundPeg` ile çalışır.
- `SquarePeg` → mevcut sistemle uyumsuz sınıf (adaptee).
- `SquarePegAdapter` → `RoundPeg` gibi davranır ama içinde `SquarePeg` taşır.

### Nasıl çalışıyor?

`RoundHole.fits(...)` metodu yarıçap ile kontrol yapıyor.
`SquarePeg` ise yalnızca genişlik (`width`) biliyor.

Adapter, kare çivinin deliğe sığabilecek eşdeğer yarıçapını hesaplar:

```text
radius = (width * sqrt(2)) / 2
```

Bu hesap, kareyi çevreleyen en küçük çember yarıçapıdır.

- `width=5` olan kare çivi adapter ile sığar,
- `width=10` olan kare çivi adapter ile sığmaz.

---

## Roller (Pattern Yapısı)

- **Client**: `AdapterPatternDemo`
- **Client Interface / Target**: `RoundPeg`
- **Service / Adaptee**: `SquarePeg`
- **Adapter**: `SquarePegAdapter`

İstemci sadece target tipine (`RoundPeg`) bağımlı kaldığı için, yeni adapter türleri kolayca eklenebilir.

---

## Ne zaman kullanılır?

- Legacy bir sınıfı yeni sisteme entegre ederken,
- 3rd-party kütüphane arayüzü mevcut kodla uyuşmadığında,
- Mevcut sınıfı değiştirmek riskli veya mümkün değilse.

---

## Artılar / Eksiler

### Artılar
- Single Responsibility: dönüşüm mantığı ayrı sınıfta toplanır.
- Open/Closed: yeni adapter ekleyerek sistemi genişletebilirsin.
- Mevcut kodu ve dış servisi bozmadan entegrasyon sağlar.

### Eksiler
- Ek sınıf katmanı getirir, yapı biraz karmaşıklaşır.
- Küçük projelerde "doğrudan değiştir geç" yaklaşımı daha basit olabilir.

---

## Kısa Özet

Adapter, "uyumsuz arayüzleri konuşturan çevirmen"dir.
Bu projede `SquarePegAdapter`, kare çiviyi yuvarlak çivi gibi göstererek `RoundHole` ile uyumlu hale getirir.
