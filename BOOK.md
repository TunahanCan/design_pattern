# Creational Design Patterns — Mini Book (Java)

Bu doküman, **Refactoring.Guru'daki Creational Patterns** yaklaşımını takip ederek hazırlanmış kısa bir öğrenme kitabıdır.

## İçerik

1. Factory Method
2. Abstract Factory
3. Builder
4. Prototype
5. Singleton

---

## 1) Factory Method

**Amaç:** Nesne oluşturma işini alt sınıflara devretmek.

**Ne zaman kullanılır?**
- Hangi sınıfın oluşturulacağını çalışma anında seçmek istediğinde.
- `new` bağımlılığını merkezileştirmek istediğinde.

**Projede örnek:**
- `NotificationCreator` soyut üretici sınıfı.
- `EmailNotificationCreator`, `SmsNotificationCreator`, `PushNotificationCreator` farklı ürünleri üretir.

---

## 2) Abstract Factory

**Amaç:** Birbiriyle ilişkili ürün ailelerini, somut sınıflara bağlanmadan üretmek.

**Ne zaman kullanılır?**
- Bir UI temasının tüm bileşenlerini (button, checkbox gibi) birlikte değiştirmek gerektiğinde.

**Projede örnek:**
- `GuiFactory` arayüzü `Button` ve `Checkbox` üretir.
- `LightThemeFactory` ve `DarkThemeFactory` farklı ama uyumlu ürün aileleri üretir.

---

## 3) Builder

**Amaç:** Karmaşık nesneleri adım adım inşa etmek.

**Ne zaman kullanılır?**
- Çok sayıda opsiyonel alan varsa.
- Constructor patlamasından kaçınmak istiyorsan.

**Projede örnek:**
- `Report.Builder` ile başlık, özet, bölüm listesi ve grafik seçeneği adım adım set edilir.

---

## 4) Prototype

**Amaç:** Mevcut bir nesneyi kopyalayarak yeni nesneler üretmek.

**Ne zaman kullanılır?**
- Nesne oluşturma maliyetliyse veya yapı karmaşıksa.
- Benzer nesneleri hızlı türetmek gerekiyorsa.

**Projede örnek:**
- `Resume` nesnesi `copy()` ile çoğaltılır.
- Kopya üzerinden sadece isim gibi değişken alanlar güncellenir.

---

## 5) Singleton

**Amaç:** Bir sınıftan uygulama boyunca tek bir instance olmasını garanti etmek.

**Ne zaman kullanılır?**
- Konfigürasyon, cache, logger gibi tekil paylaşılacak kaynaklarda.

**Projede örnek:**
- `AppConfig` sınıfı double-checked locking ile thread-safe tek instance sağlar.

---

## Projeyi Çalıştırma

```bash
mvn -q clean compile
mvn -q exec:java -Dexec.mainClass="com.can.Main"
```

> Not: Eğer `exec-maven-plugin` yoksa IDE üzerinden `com.can.Main` çalıştırılabilir.

## Dosya Yapısı

- `src/main/java/com/can/Main.java`
- `src/main/java/com/can/creational/factorymethod/FactoryMethodDemo.java`
- `src/main/java/com/can/creational/abstractfactory/AbstractFactoryDemo.java`
- `src/main/java/com/can/creational/builder/BuilderDemo.java`
- `src/main/java/com/can/creational/prototype/PrototypeDemo.java`
- `src/main/java/com/can/creational/singleton/SingletonDemo.java`

Bu yapı ile her pattern bağımsız anlaşılır, ayrıca `Main` üzerinden toplu gösterim yapılır.
