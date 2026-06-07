# Robot Süpürge Simülasyonu — BZ 214

## Proje Yapısı

```
src/
├── common/          ← Ortak sabitler, interface'ler, enum'lar
├── model/           ← Kişi 1: Cell, Room, Robot, Dirt, PathFinder, Algoritmalar
├── controller/      ← Kişi 3: SimulationController, AlgorithmFactory, ...
├── view/            ← Kişi 2: Main, GridView, ControlPanel, StatusBar, ...
└── sounds/          ← Ses dosyaları (clean.wav, charge.wav, complete.wav)
```

## IntelliJ IDEA Kurulumu

### 1. Projeyi Aç
File → Open → bu klasörü seç

### 2. JavaFX Ekle
File → Project Structure → Libraries → + → Maven
`org.openjfx:javafx-controls:17`
`org.openjfx:javafx-fxml:17`
`org.openjfx:javafx-media:17`  ← ses için

### 3. src/ Klasörünü Source Root Yap
`src/` klasörüne sağ tık → Mark Directory as → Sources Root

### 4. sounds/ Klasörünü Resources Root Yap
`src/sounds/` → Mark Directory as → Resources Root
(Böylece `/sounds/clean.wav` yolu çalışır)

### 5. Çalıştır
`view/AppLauncher.java` → sağ tık → Run AppLauncher.main()

## Düzeltilen Hatalar

| # | Hata | Düzeltme |
|---|------|----------|
| 1 | Main.java mock nesneler kullanıyordu → simülasyon çalışmıyordu | Gerçek Room, Robot, SimulationController oluşturuldu |
| 2 | GridView farklı room referansı → tıklanan engel görünmüyordu | controller.getRoom() aynı referans GridView'a verildi |
| 3 | Engel içinden geçiş | tick() → move() öncesi room.isWalkable() kontrolü eklendi |
| 4 | Kir temizlenmiyordu | cleanCurrentCell() sırası düzeltildi: incrementDirtProgress → eşik kontrolü → clearDirt |
| 5 | setBattery() bataryayı artıramıyordu | chargeTick() döngüsüyle hedef seviyeye çıkarma eklendi |
| 6 | Robotun bulunduğu hücreye engel eklenebiliyordu | addObstacle() içinde robot konumu kontrolü |
| 7 | returnToStation() running bayrağını bozuyordu | Doğrudan timeline başlatma, startSimulation() çağrılmıyor |
| 8 | clearDirt() sonrası setCleaned(true) çift çağrısı | clearDirt() zaten setCleaned(true) yapıyor — çift çağrı kaldırıldı |
| 9 | Spiral/WallFollow sonraki iterasyonda eski state | Algoritmalar doğrudan IRobot state'i okuyor — sorun yok |

## Kullanım

| İşlem | Nasıl |
|-------|-------|
| Engel ekle | Sol panel → "Mobilya" seç → Canvas'a sol tıkla |
| Kir ekle | Sol panel → "Toz/Sıvı/Leke" seç → Canvas'a sol tıkla |
| Sil | Canvas'a SAĞ tıkla |
| Başlat | "▶ Başlat" |
| Duraklat | "⏸ Duraklat" |
| Sıfırla | "↺ Sıfırla" |
| İstasyona dön | "⚡ İstasyona Dön" |
| Hız değiştir | Slider: 0.5x – 3.0x |
| Batarya ayarla | Açılır liste → Ayarla |
