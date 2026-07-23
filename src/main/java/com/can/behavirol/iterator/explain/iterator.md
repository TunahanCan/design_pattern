# Iterator — Yineleyici Deseni

> Bu örnek custom bir iterator sözleşmesi kullanır.
> Java’nın standart `Iterator` API’siyle aynı bitiş davranışına sahip değildir.

## 30 saniyelik kart

| Soru | Kısa cevap |
|---|---|
| Niyet | Koleksiyonun iç yapısını açmadan elemanları sırayla gezmek |
| Değişen eksen | Gezinme türü, filtre, sıra ve cursor state’i |
| Ana bedel | Ek iterator nesneleri ve koleksiyon değişince snapshot tutarlılığı |
| Bu örnekte | Aynı sosyal grafikte friends ve coworkers gezintileri |
| Hafıza ipucu | “Koleksiyonu verme; sıradaki elemanı sorabileceğim bir kumanda ver.” |

## Örnek haritası: temel → güçlendirilmiş → production

| Katman | Bu repoda ne var? | Öğrettiği sınır |
|---|---|---|
| Temel örnek | `SocialGraphIterator` ile friends/coworkers gezintisi | Koleksiyon yapısını client’tan saklama ve bağımsız cursor state’i |
| Güçlendirilmiş örnek | `CompanyProfileIterator` | Var olan iterator’ı açmadan lazy filtreyle compose etme ve `hasMore()` için buffer tutma |
| Production sınırı | Java `Iterator`/`Spliterator`, sayfalama, retry, snapshot/version politikası | Null sentinel ve process içi cache’in büyük/uzak veri kaynağına yetmemesi |

## Akılda kalıcı analoji: müze sesli rehberi

Bir müzenin depolama planını bilmezsin.
Sesli rehber sana yalnız:

- sırada ziyaret edilecek eseri,
- gezi bitip bitmediğini,
- seçtiğin rotanın sırasını

söyler.

Çocuk rotasıyla tarih rotası aynı müzeyi farklı sırayla gezebilir.
Her cihaz kendi kaldığı yeri hatırlar.
Iterator da koleksiyon ile gezinme state’ini bu şekilde ayırır.

## Pattern olmasaydı problem ne olurdu?

Client doğrudan graph map’lerini bilirse traversal ayrıntısı dışarı sızar:

```java
for (String friendId : friendsByProfileId.get(profileId)) {
    Profile profile = profiles.get(friendId);
    send(profile.email());
}
```

Bu kod:

- map ve list yapısına bağımlıdır,
- dangling ID politikasını client’a taşır,
- friends ve coworkers için tekrarlanır,
- cursor’ı durdurup devam ettirmeyi zorlaştırır,
- veri kaynağı uzaktaki API olursa yeniden yazılır.

## Çözüm

`ProfileIterator`, client’a yalnız iki operasyon sunar:

```java
boolean hasMore();
Profile getNext();
```

`SocialGraphIterator` profil ID’sini, relation türünü, cursor’ı ve lazy cache’i kendi içinde tutar.
`Facebook`, istenen relation için doğru iterator’ı üreten aggregate/factory rolündedir.
`SocialSpammer`, graph yapısını görmeden iterator üzerinden ilerler.

### Çözmediği şeyler

Iterator tek başına:

- graph verisini thread-safe yapmaz,
- veri kaynağının değişmesi halinde tutarlılık garantilemez,
- duplicate veya dangling relation’ı doğrulamaz,
- pagination, retry ve rate limit sağlamaz,
- doğru bitiş sözleşmesini otomatik seçmez.

## Repodaki roller

| Pattern rolü | Repodaki karşılığı | Sorumluluk |
|---|---|---|
| Iterator | `ProfileIterator` | `hasMore` ve `getNext` sözleşmesi |
| Concrete Iterator | `SocialGraphIterator` | Cursor, relation seçimi ve lazy cache |
| Iterator decorator | `CompanyProfileIterator` | Delegate iterator’ı şirket adına göre lazy süzer |
| Aggregate arayüzü | `SocialNetwork` | Friends/coworkers iterator factory’leri |
| Concrete Aggregate | `Facebook` | `SocialGraphIterator` üretir |
| Veri sahibi | `SocialGraph` | Profile ve relation map’lerini okur |
| Element | `Profile` | Gezilen immutable record |
| Relation seçimi | `RelationType` | `FRIENDS` veya `COWORKERS` |
| Client | `SocialSpammer` | Iterator’dan email adreslerini tüketir |
| Composition root | `IteratorPatternDemo` | Örnek graph’ı kurar |

## Yapı

```mermaid
classDiagram
    class ProfileIterator {
        <<interface>>
        +hasMore() boolean
        +getNext() Profile
    }
    class SocialGraphIterator {
        -socialGraph SocialGraph
        -profileId String
        -relationType RelationType
        -currentPosition int
        -cache List~Profile~
    }
    class CompanyProfileIterator {
        -delegate ProfileIterator
        -company String
        -bufferedProfile Profile
        +hasMore() boolean
        +getNext() Profile
    }
    class SocialNetwork {
        <<interface>>
        +createFriendsIterator(id)
        +createCoworkersIterator(id)
    }
    ProfileIterator <|.. SocialGraphIterator
    ProfileIterator <|.. CompanyProfileIterator
    CompanyProfileIterator o--> ProfileIterator : lazy delegate
    SocialNetwork <|.. Facebook
    Facebook --> SocialGraphIterator
    SocialGraphIterator --> SocialGraph
    SocialSpammer --> ProfileIterator
```

## Çalışma akışı

```mermaid
sequenceDiagram
    participant Client as SocialSpammer
    participant Filter as CompanyProfileIterator
    participant Delegate as SocialGraphIterator
    participant Graph as SocialGraph
    Client->>Filter: hasMore()
    Filter->>Delegate: hasMore()
    Delegate->>Graph: getFriendsOf(profileId) (ilk erişim)
    Graph-->>Delegate: List~Profile~
    loop eşleşme bulunana veya delegate bitene kadar
        Filter->>Delegate: getNext()
        Delegate-->>Filter: candidate
        Filter->>Filter: company eşleşmesini kontrol et
    end
    Filter->>Filter: bufferedProfile = match
    Filter-->>Client: true
    Client->>Filter: hasMore() (tekrar)
    Filter-->>Client: true (delegate ilerlemez)
    Client->>Filter: getNext()
    Filter-->>Client: buffered match döner ve buffer temizlenir
```

## Kod execution trace

1. Demo profile ve relation map’lerini oluşturur.
2. `Facebook#createFriendsIterator("1")` yeni iterator üretir.
3. Constructor graph’a henüz sorgu yapmaz.
4. İlk `hasMore()` çağrısı `lazyInit()` metodunu tetikler.
5. Graph ID listesini profile listesine dönüştürür.
6. Bulunamayan profile ID’leri `filter` ile sessizce çıkarılır.
7. Elde edilen `Stream.toList()` sonucu iterator cache’ine yazılır.
8. `hasMore`, cursor cache boyutundan küçükse true döner.
9. `getNext`, mevcut profile’ı alıp cursor’ı bir artırır.
10. Bitişte `getNext()` null döner.

Friends ve coworkers iterator’ları ayrı nesnelerdir.
Bu yüzden cursor state’leri birbirini bozmaz.
Bu ifade thread-safe oldukları anlamına gelmez.

`CompanyProfileIterator`, delegate’den eşleşen profile ulaşana kadar ilerler.
`hasMore()` sırasında bulunan eşleşmeyi buffer’da sakladığı için tekrarlı `hasMore()` çağrıları sonucu tüketmez; `getNext()` buffer’ı döndürüp temizler.

## API, invariant ve sonuç semantiği

- Yeni `SocialGraphIterator` nesnesinin `currentPosition` değeri sıfırdır.
- `SocialGraphIterator#hasMore()` cursor’ı ilerletmez.
- `SocialGraphIterator#getNext()` başarılı olduğunda cursor’ı tam bir artırır.
- Bitişte `hasMore()` false, `getNext()` null döner.
- Bu null sentinel, Java `Iterator#next()` sözleşmesinden farklıdır.
- Relation listesinin sırası sonuç sırasını belirler.
- Duplicate ID duplicate profile üretebilir.
- Bilinmeyen source profile boş relation gibi davranır.
- Graph’ta bulunmayan relation ID’si sessizce filtrelenir.
- Cache ilk erişimde oluşur ve daha sonra yenilenmez.
- `CompanyProfileIterator` şirket adını case-insensitive karşılaştırır.
- Filtre iterator’ının buffer’ı en fazla bir `Profile` taşır.
- Filtre iterator’ının ilk `hasMore()` çağrısı eşleşme ararken delegate cursor’ını ilerletebilir.
- Eşleşme buffer’a girdikten sonraki tekrarlı `hasMore()` çağrıları delegate’i yeniden ilerletmez.

## Lazy cache ve mutation zamanı

`SocialGraph` constructor girdilerini defensive copy yapmadan saklar.
Bu nedenle dışarıdaki mutable map/list:

- iterator oluşturulduktan sonra,
- fakat ilk `hasMore/getNext` öncesinde

değişirse yeni veri cache’e yansıyabilir.
Cache oluştuktan sonraki değişiklikler aynı iterator’da görünmez.
Bu davranış snapshot zamanının ilk erişim olduğunu gösterir.

## Test kontrat haritası

| `@Nested` grup | Korunan davranış |
|---|---|
| `RelationTraversal` | Friends sırası ve coworkers filtresi |
| `CursorState` | Idempotent `hasMore`, ilerleme, bağımsız iterator, null bitiş |
| `EmptyTraversal` | Bilinmeyen source ve dangling relation davranışı |
| `IteratorComposition` | Lazy şirket filtresi ve buffered `hasMore` idempotency’si |

Testler client’ın graph map’lerine erişmesini gerektirmez.
İki iterator interleaved ilerletilerek cursor isolation doğrudan gözlenir.

## Edge case, security, concurrency ve performans

- Null `relationType`, mevcut ternary nedeniyle coworkers koluna düşer.
- Enum’a yeni değer eklenirse o da sessizce coworkers gibi davranır.
- `switch` ile exhaustive seçim daha güvenli olurdu.
- Graph girdileri defensive copy değildir.
- Iterator ve graph thread-safe değildir.
- Remote graph’ta lazy init ilk çağrıyı beklenmedik biçimde yavaşlatabilir.
- `SocialGraphIterator` cache oluştuktan sonra `hasMore/getNext` için O(1) çalışır.
- `CompanyProfileIterator#hasMore`, sıradaki eşleşmeye kadar k adayı tararsa O(k) çalışır; bütün traversal toplamda O(n)’dir.
- `SocialSpammer` adı ve davranışı production ileti izinlerini modellemez.

Production’da standart `Iterator`, `Iterable`, stream, cursor pagination veya reactive publisher seçenekleri değerlendirilmelidir.

## Ne zaman kullan?

- Veri yapısını client’tan gizlemek istiyorsan,
- aynı veri üzerinde birden çok traversal gerekiyorsa,
- gezinmeyi durdurup devam ettirmek gerekiyorsa,
- koleksiyon ileride listeden tree/API’ye dönüşebilecekse,
- client yalnız eleman sırasını tüketmeliyse.

## Ne zaman kullanma?

- Basit bir `List` ve for-each yeterliyse,
- bütün veri tek seferde map/filter operasyonuyla işlenecekse,
- snapshot tutarlılığı tanımlanmamış mutable kaynakta kritikse,
- async/backpressure gereken akış custom sync iterator’a sıkıştırılıyorsa.

## Artılar ve bedeller

| Artı | Bedel |
|---|---|
| Koleksiyon temsili gizlenir | Ek arayüz ve sınıflar oluşur |
| Cursor ayrı nesnede yaşar | Iterator invalidation politikası gerekir |
| Birden çok bağımsız gezinti olur | Cache bellek tüketebilir |
| Client traversal ayrıntısını bilmez | Hata ve bitiş sözleşmesi tasarlanmalıdır |
| Farklı sıra/filtre eklenebilir | Yeni relation mevcut aggregate API’sini değiştirebilir |

## En çok karışan desen: Strategy

| Iterator | Strategy |
|---|---|
| Bir yapıda nasıl ilerlediğini ve nerede kaldığını taşır | Bir işi hangi algoritmayla yaptığını taşır |
| Cursor state’i tipiktir | Algoritmalar çoğunlukla bağımsızdır |
| Sonuç eleman dizisidir | Sonuç tek hesaplama olabilir |
| Aggregate ile birlikte çalışır | Context ile birlikte çalışır |

Bir traversal’ın sıralama algoritması Strategy olabilir; gezinti state’i yine Iterator’da kalır.

## Yaygın hatalar

1. Bitiş davranışını dokümante etmemek.
2. `hasMore()` içinde cursor ilerletmek.
3. İki iterator’a ortak cursor vermek.
4. Mutable koleksiyonun snapshot zamanını belirsiz bırakmak.
5. Null relation’ı varsayılan kola düşürmek.

## Alıştırmalar

### Seviye 1 — Gözlemle

İki friends iterator üret.
Birini iki adım, diğerini bir adım ilerlet ve cursor’ların bağımsızlığını test et.

### Seviye 2 — Tasarla

`ProfileIterator`ı Java `Iterator<Profile>` sözleşmesine uyarla.
Bitişte null yerine `NoSuchElementException` bekleyen test yaz.

### Seviye 3 — Ölçekle

Graph’ın uzak API’den sayfa sayfa geldiğini varsay.
Cursor token, retry, cache ve rate-limit davranışlarını içeren lazy iterator tasarla.

## Hafıza cümlesi

**Iterator, koleksiyonun kapısını açmaz; sana yalnız sıradaki elemanı ve yolun bitip bitmediğini söyler.**
