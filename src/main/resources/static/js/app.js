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

// =====================================================================
// Top navigation loading bar (NProgress-style).
//
// In PWA standalone mode the browser URL bar is hidden, so users get no
// visual signal that a click triggered navigation — the app feels frozen
// for the 200-2000ms until the new page renders. We add a red bar at the
// top of the viewport that fires on internal link click / form submit,
// animates to ~85% (slow tail), and completes when `pageshow` fires on
// the next page. Server-side rendering means skeleton loaders wouldn't
// help here — the HTML arrives already populated.
// =====================================================================
(function setupLoadingBar() {
  let bar = null;
  let timer = null;
  let active = false;

  function ensureBar() {
    if (bar) return bar;
    bar = document.createElement('div');
    bar.className = 'nav-progress';
    document.body.appendChild(bar);
    return bar;
  }

  function setClass(cls) {
    const b = ensureBar();
    // Force reflow so consecutive transitions restart cleanly.
    void b.offsetWidth;
    b.className = 'nav-progress ' + cls;
  }

  function startBar() {
    if (active) return;
    active = true;
    setClass('start');
    requestAnimationFrame(() => {
      requestAnimationFrame(() => setClass('loading'));
    });
    // Safety net: if pageshow never fires (navigation off-site, slow
    // network with no commit), hide the bar after 10s so it doesn't
    // hang forever.
    clearTimeout(timer);
    timer = setTimeout(finishBar, 10000);
  }

  function finishBar() {
    if (!active) return;
    active = false;
    clearTimeout(timer);
    setClass('complete');
    setTimeout(() => {
      setClass('hide');
      setTimeout(() => {
        if (bar) bar.className = 'nav-progress';
      }, 400);
    }, 200);
  }

  // ---- Triggers: internal link clicks ----
  document.addEventListener('click', (e) => {
    if (e.defaultPrevented || e.button !== 0 ||
        e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
    const a = e.target.closest('a[href]');
    if (!a) return;
    if (a.target === '_blank' || a.hasAttribute('download')) return;
    const href = a.getAttribute('href') || '';
    // Skip external, mailto:, tel:, javascript:.
    if (/^(mailto:|tel:|javascript:)/i.test(href)) return;
    if (/^https?:/i.test(href) && !href.startsWith(location.origin)) return;
    // Skip same-page hash-only links (e.g., #section).
    try {
      const url = new URL(a.href, location.href);
      if (url.pathname === location.pathname &&
          url.search === location.search && url.hash) return;
    } catch (_) { /* relative href — proceed */ }
    startBar();
  });

  // ---- Triggers: form submit (login, register, OTP, admin, etc.) ----
  document.addEventListener('submit', (e) => {
    if (e.defaultPrevented) return;
    startBar();
  });

  // ---- Resolve when new page is committed (also covers BFCache restore) ----
  window.addEventListener('pageshow', finishBar);
  // If this script runs after page is already complete (cached SW),
  // resolve immediately so a stale bar doesn't linger.
  if (document.readyState === 'complete') finishBar();
})();

// =====================================================================
// Mobile hamburger menu toggle.
//
// The button is hidden on desktop (CSS @media max-width:768px reveals it).
// This handler flips aria-expanded (drives the hamburger↔X animation via
// CSS attribute selector) and toggles .nav-open on <header> (drives the
// dropdown expansion via max-height transition). Menu auto-closes on link
// click, Escape, or page transition.
// =====================================================================
(function setupNavToggle() {
  const header = document.querySelector('header');
  const toggle = header && header.querySelector('.nav-toggle');
  const nav = header && header.querySelector('nav');
  if (!header || !toggle || !nav) return;

  function setOpen(open) {
    toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    header.classList.toggle('nav-open', open);
  }

  toggle.addEventListener('click', () => {
    const isOpen = toggle.getAttribute('aria-expanded') === 'true';
    setOpen(!isOpen);
  });

  // Close when any nav link is clicked (navigation follows immediately).
  nav.addEventListener('click', (e) => {
    if (e.target.closest('a')) setOpen(false);
  });

  // Close on Escape.
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') setOpen(false);
  });

  // Reset on page show (covers fresh load + BFCache restore) so a menu
  // left open in a cached state doesn't bleed into the next page.
  window.addEventListener('pageshow', () => setOpen(false));
})();
