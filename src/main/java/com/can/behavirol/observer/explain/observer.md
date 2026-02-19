# Observer Pattern (Gözlemci Deseni)

**Diğer adları:** Event-Subscriber, Listener  
**Tür:** Behavioral (Davranışsal)

## Amaç (Intent)

Observer, bir nesnede (publisher/subject) meydana gelen değişikliklerin o nesneyi izleyen birden fazla nesneye (subscriber/observer) otomatik olarak bildirilmesini sağlar.

Bu sayede:

- Publisher ile subscriber sınıfları gevşek bağlı kalır.
- Abonelikler çalışma anında dinamik olarak eklenip çıkarılabilir.
- Yeni subscriber eklemek için mevcut publisher kodunu değiştirmek gerekmez.

---

## Problem

Mağazaya yeni bir ürün geldiğinde müşterilere bilgi vermek istiyoruz.

- Müşteri her gün mağazayı kontrol ederse zaman kaybı olur.
- Mağaza herkese sürekli bildirim atarsa spam oluşur.

Yani doğru kitleye, doğru anda, dinamik şekilde haber vermek gerekir.

---

## Çözüm

Observer deseniyle mağaza bir **Publisher**, müşteriler ise **Subscriber** olur.

- Müşteri ilgilendiği ürüne abone olur (`subscribe`).
- İlgisi bittiğinde abonelikten çıkar (`unsubscribe`).
- Ürün stok değişince mağaza sadece o ürüne abone olanları bilgilendirir (`notifySubscribers`).

---

## Projedeki OOP Örneği

Bu projede örnek akış şu sınıflarla kuruldu:

- `Publisher` → abonelik sözleşmesi (subscribe/unsubscribe/notify)
- `Subscriber` → bildirim sözleşmesi (`update`)
- `Store` → **Concrete Publisher**, ürün bazlı aboneleri tutar
- `Customer` → **Concrete Subscriber**, bildirimi farklı kanallardan alır
- `ObserverPatternDemo` → örnek çalışma senaryosu

### Akış

1. `Can` ve `Ayşe`, `iPhone 16` için abone olur.
2. `Mehmet`, `PlayStation 6` için abone olur.
3. `Store.restockProduct("iPhone 16", 12)` çağrıldığında sadece iPhone 16 aboneleri bilgilendirilir.
4. `Ayşe` abonelikten çıkar.
5. İkinci stok güncellemesinde sadece kalan aboneler bildirim alır.

Bu yapı Observer’ın özünü gösterir: **bir kaynaktaki değişiklik, o kaynağa abone olan çoklu nesnelere otomatik yayılır**.

---

## Ne zaman kullanılmalı?

- Bir nesnenin durum değişikliği birden fazla nesneyi etkileyecekse,
- Bu nesnelerin sayısı önceden bilinmiyorsa veya dinamik değişiyorsa,
- Gevşek bağlı, olay tabanlı bir iletişim istiyorsan.

---

## Artılar / Eksiler

### Artılar

- Open/Closed Principle ile uyumludur.
- Runtime’da abone ekleme/çıkarma kolaydır.
- Publisher, concrete subscriber sınıflarını bilmez.

### Eksiler

- Bildirim sırası garanti edilmez.
- Çok fazla observer olduğunda hata ayıklama zorlaşabilir.
