# AGENTS.md - Sandbox Development Guidelines & Rules

Dokumen ini adalah pedoman dan aturan perilaku (rules of engagement) bagi AI Agent dalam mendampingi pengembangan project `spring_boot_sandbox`.

---

## 1. Scope & Code Modification Rules (Batasan Coding)
- **Folder `sandbox/` (Backend & Infra): STRICT NO-CODE ZONE.**
  - DILARANG mengedit, membuat, atau menghapus kode di dalam folder `sandbox/` secara mandiri kecuali ada instruksi eksplisit dari user (contoh: *"tolong edit file X"*, *"buatkan file Y di sandbox"*).
  - **Sebagai gantinya:** Berikan panduan (guidance) yang jelas, terstruktur, lengkap langkah demi langkah, namun tetap **compact** (padat, ringkas, to the point, dan mudah diikuti).
- **Folder `frontend/`:**
  - Agent diperbolehkan membantu coding di folder `frontend/` jika memang relevan dengan task atau diminta oleh user.
- **Prinsip Umum:** Jangan menulis kode apapun di luar apa yang diminta.

---

## 2. Gaya Komunikasi, Rekomendasi, & Analisis
- **Konsultatif & Jujur:** Jawaban harus memuat opini profesional, rekomendasi terbaik, dan kritik membangun. Jangan ragu memberikan evaluasi objektif terhadap arsitektur atau kode yang kurang tepat.
- **Komparasi Alternatif:** Jika terdapat beberapa pendekatan (misal: Kafka vs RabbitMQ, Eureka vs Kubernetes Service Discovery, Stateful vs Stateless Auth, dsb.), sertakan perbandingan kelebihan dan kekurangan (*pros & cons / trade-offs*) serta opsi mana yang paling direkomendasikan dan alasannya.
- **Format:** Ringkas, terstruktur, gunakan diagram/tabel jika membantu pemahaman konsep.

---

## 3. Version & Environment Awareness
Setiap panduan, dependensi, konfigurasi YAML, maupun snippet kode wajib memperhatikan versi teknologi yang terpasang di project ini agar selalu kompatibel dan relevan:
- **Java:** JDK 17
- **Spring Boot Starter Parent:** `4.1.1` (atau versi yang tertera di root `sandbox/pom.xml`)
- **Spring Cloud:** `2025.1.3` (atau versi BOM terkait)
- **MapStruct & Lombok:** MapStruct `1.6.3`, `lombok-mapstruct-binding` `0.2.0`
- **Build Tool:** Apache Maven
- **Infrastruktur / Container:** Docker & Docker Compose (PostgreSQL, RabbitMQ, Prometheus, Grafana, pgAdmin)
- **Frontend:** Vite, HTML/CSS/JavaScript

*Catatan: Selalu verifikasi konfigurasi `pom.xml` dan versi library sebelum menyarankan dependensi baru untuk mencegah class not found atau breaking changes antar versi Spring Boot.*

---

## 4. Konsep Project: Sandbox "Gado-Gado" untuk Perbankan (Banking Domain)
Project ini adalah sandbox eksperimental ("gado-gado") untuk menguji dan mempraktikkan fitur-fitur kelas enterprise yang biasa diterapkan di industri, khususnya di bidang **Digital Banking / Financial Technology**:

### Arsitektur Modul / Layanan
1. **Gateway Service (`gateway-service`):**
   - API Gateway, routing, rate limiting, centralized JWT verification / token relay.
2. **Auth & Identity Service (`auth-service`):**
   - User authentication, token issuance (JWT), role-based access control (RBAC), multi-factor authentication (MFA/OTP flow).
3. **User & Customer Service (`user-service`):**
   - Manajemen profil nasabah (Customer Information File / CIF), KYC status, preferensi akun.
4. **Account & Transaction Service (Core Banking - Rencana Pengembangan):**
   - Manajemen rekening (Savings, Checking, Deposit).
   - Mutasi, transfer dana (intra-bank & inter-bank simulator), top-up, pembayaran tagihan.
   - **Critical enterprise patterns:** Idempotency Key (mencegah double-debit), Double-Entry Bookkeeping (Ledger debit/kredit seimbang), Distributed Transactions (Saga Pattern).
5. **Notification Service (`notification-service`):**
   - Event-driven notifications via RabbitMQ (email, SMS, WhatsApp mock, push notifications untuk alert transaksi dan OTP).
6. **File / Reporting Service (`file-service`):**
   - Rekening koran (e-statement generator format PDF/Excel), export histori transaksi, upload dokumen pendukung KYC.
7. **Discovery & Observability (`discovery-service`, Prometheus, Grafana):**
   - Service discovery (Eureka), centralized metrics scraping, application health check, visual dashboard monitoring.

---

## 5. Prosedur Diagnosis Error (Troubleshooting Protocol)
Ketika user mengirimkan pesan atau log error:
1. **Riset & Validasi:** Jangan langsung memberikan tebakan cepat tanpa dasar. Lakukan pengecekan context terlebih dahulu (baca file terkait, konfigurasi yang sedang aktif, atau file log).
2. **Jelaskan Root Cause:** Uraikan kenapa error tersebut terjadi secara teknis (bukan sekadar mengulang isi pesan error).
3. **Identifikasi Potensi Penyebab:** Jika ada beberapa kemungkinan faktor pemicu, jelaskan kemungkinannya secara berurutan mulai dari yang paling sering terjadi.
4. **Panduan Perbaikan Langkah-demi-Langkah:** Berikan solusi yang presisi, compact, dan langsung dapat dieksekusi oleh user tanpa perlu mengutak-atik kode secara otomatis di folder `sandbox/`.
