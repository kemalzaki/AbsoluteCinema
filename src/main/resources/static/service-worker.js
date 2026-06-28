// Version history:
//   v2 → v3: purge stale SW state from SecurityConfig redirect era.
//   v3 → v4: re-cache updated CSS (loading bar) + JS (loading bar + PWA prompt).
//   v4 → v5: re-cache updated CSS (hamburger dropdown) + JS (nav toggle).
//   v5 → v6: drop Thymeleaf fragment parameter (was breaking non-home pages);
//            switch to body.page-home CSS class for wordmark hiding.
const CACHE_NAME = 'absolute-cinema-v6';
const ASSETS_TO_CACHE = [
  '/',
  '/katalog',
  '/css/style.css',
  '/js/app.js',
  '/manifest.json',
  '/service-worker.js',
  '/images/icon-192.png',
  '/images/icon-512.png',
  '/images/icon.svg',
  '/images/poster-placeholder.svg'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('Opened cache');
        // addAll fails atomically if any request errors; keep core assets robust.
        return Promise.allSettled(
          ASSETS_TO_CACHE.map(url =>
            cache.add(url).catch(err => console.warn('SW cache miss:', url, err))
          )
        );
      })
      .then(() => self.skipWaiting())
  );
});

self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET') return;

  event.respondWith(
    fetch(event.request)
      .catch(() => {
        return caches.match(event.request)
          .then(response => {
            if (response) {
              return response;
            }
            if (event.request.mode === 'navigate') {
              return caches.match('/');
            }
            return new Response('Network error happened', {
              status: 408,
              headers: { 'Content-Type': 'text/plain' },
            });
          });
      })
  );
});

self.addEventListener('activate', event => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Pesan dari page → langsung aktifkan SW baru tanpa tunggu semua tab tutup.
self.addEventListener('message', event => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});
