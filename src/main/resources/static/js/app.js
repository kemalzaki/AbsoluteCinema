// =====================================================================
// PWA: Service Worker registration + install prompt handling.
//
// Sebelumnya SW gagal register karena /service-worker.js di-redirect ke
// /login oleh SecurityConfig (matcher permitAll tidak mencakup root file).
// Setelah SecurityConfig diperbaiki (menambahkan "/manifest.json" dan
// "/service-worker.js" ke permitAll), SW seharusnya register & controlling.
// =====================================================================

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/service-worker.js', { scope: '/' })
      .then(registration => {
        console.log('[PWA] SW registered with scope:', registration.scope);
        // Pastikan update diambil bila ada versi baru (avoid stale SW).
        if (registration.waiting) {
          registration.waiting.postMessage({ type: 'SKIP_WAITING' });
        }
        registration.addEventListener('updatefound', () => {
          const newWorker = registration.installing;
          if (newWorker) {
            newWorker.addEventListener('statechange', () => {
              if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                // SW baru sudah siap; reload sekali agar page dipimpin SW baru.
                console.log('[PWA] New SW installed — reloading to activate.');
                window.location.reload();
              }
            });
          }
        });
      }, err => {
        console.error('[PWA] SW registration failed:', err);
      });

    // Reload saat controller berganti (SW baru mengambil alih).
    let refreshing = false;
    navigator.serviceWorker.addEventListener('controllerchange', () => {
      if (refreshing) return;
      refreshing = true;
      window.location.reload();
    });
  });
}

// =====================================================================
// Install prompt. Chrome akan fire `beforeinstallprompt` ketika semua
// kriteria installability terpenuhi (manifest lengkap + SW aktif + HTTPS
// + icons). Kita simpan event-nya supaya bisa dipicu dari tombol custom
// kalau nanti ditambahkan, dan log status untuk debugging.
// =====================================================================

let deferredInstallPrompt = null;

window.addEventListener('beforeinstallprompt', (event) => {
  // Cegah Chrome menampilkan prompt default di timing yang buruk.
  event.preventDefault();
  deferredInstallPrompt = event;
  console.log('[PWA] beforeinstallprompt fired — app is installable.');
  // Tanda bahwa ikon install bisa ditampilkan kalau ada UI element.
  document.documentElement.setAttribute('data-installable', 'true');
});

window.addEventListener('appinstalled', () => {
  console.log('[PWA] App installed successfully.');
  deferredInstallPrompt = null;
  document.documentElement.removeAttribute('data-installable');
});

// Helper global kalau user klik tombol install custom.
// Pakai: window.triggerPwaInstall()
window.triggerPwaInstall = async () => {
  if (!deferredInstallPrompt) {
    console.log('[PWA] Install not available (either already installed or criteria not met).');
    return false;
  }
  deferredInstallPrompt.prompt();
  const { outcome } = await deferredInstallPrompt.userChoice;
  deferredInstallPrompt = null;
  return outcome === 'accepted';
};
