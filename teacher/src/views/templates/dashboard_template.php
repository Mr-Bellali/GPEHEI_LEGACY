<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <script src="https://unpkg.com/lucide@latest"></script>
    <title>EHEI - Dashboard</title>

    <style>
        .nav-pill {
            background: #3D348B;
            border-radius: 9999px;
            padding: 6px;
            display: inline-flex;
            align-items: center;
            gap: 2px;
            position: relative;
        }

        .nav-item {
            position: relative;
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 8px 16px;
            border-radius: 9999px;
            cursor: pointer;
            color: #FFFFFF;
            font-size: 14px;
            font-weight: 500;
            transition: color 0.3s ease;
            white-space: nowrap;
            z-index: 1;
            user-select: none;
        }

        .nav-item.active {
            color: #FFFFFF;
        }

        .nav-item i {
            width: 16px;
            height: 16px;
        }

        .nav-highlight {
            position: absolute;
            top: 6px;
            left: 6px;
            height: calc(100% - 12px);
            background: #7678ED;
            border-radius: 9999px;
            transition: left 0.35s cubic-bezier(0.4, 0, 0.2, 1),
                width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
            z-index: 0;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
        }

        /* Main content area */
        #main-content {
            padding-top: 80px;
            min-height: 100vh;
        }

        /* Page transitions */
        .page-wrapper {
            animation: fadeUp 0.35s ease;
        }

        @keyframes fadeUp {
            from {
                opacity: 0;
                transform: translateY(10px);
            }

            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Loading indicator */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(240, 240, 240, 0.9);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
            opacity: 0;
            visibility: hidden;
            transition: all 0.3s ease;
        }

        .loading-overlay.active {
            opacity: 1;
            visibility: visible;
        }

        .loader {
            width: 48px;
            height: 48px;
            border: 3px solid #3D348B;
            border-bottom-color: transparent;
            border-radius: 50%;
            animation: rotation 1s linear infinite;
        }

        @keyframes rotation {
            0% {
                transform: rotate(0deg);
            }

            100% {
                transform: rotate(360deg);
            }
        }

        /* Error message styling */
        .error-message {
            background: #fee2e2;
            border-left: 4px solid #ef4444;
            color: #991b1b;
        }
    </style>
</head>

<body class="w-full h-screen bg-[#F0F0F0] px-6">
    <!-- Header -->
    <div class="w-full h-[50px] flex justify-between fixed top-[14px] left-0 px-6 z-50">
        <!-- Logo & search input container -->
        <div class="h-full flex gap-4">
            <!-- Logo placeholder -->
            <div class="w-[50px] h-[50px] bg-white rounded-full flex items-center justify-center">
                <span class="text-[#3D348B] font-bold">EHEI</span>
            </div>

            <!-- Search input here -->
            <div class="flex items-center bg-white rounded-full px-5 w-60 h-[50px] gap-2.5">
                <svg class="shrink-0 text-[#404359]/50" width="18" height="18" viewBox="0 0 18 18" fill="none"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M16.6 18L10.3 11.7C9.8 12.1 9.225 12.4167 8.575 12.65C7.925 12.8833 7.2333 13 6.5 13C4.6833 13 3.146 12.3707 1.888 11.112C0.63 9.8533 0.0007 8.316 0 6.5C-0.0007 4.684 0.6287 3.1467 1.888 1.888C3.1473 0.6293 4.6847 0 6.5 0C8.3153 0 9.853 0.6293 11.113 1.888C12.373 3.1467 13.002 4.684 13 6.5C13 7.2333 12.8833 7.925 12.65 8.575C12.4167 9.225 12.1 9.8 11.7 10.3L18 16.6L16.6 18ZM6.5 11C7.75 11 8.8127 10.5627 9.688 9.688C10.5633 8.8133 11.0007 7.7507 11 6.5C10.9993 5.2493 10.562 4.187 9.688 3.313C8.814 2.439 7.7513 2.0013 6.5 2C5.2487 1.9987 4.1863 2.4363 3.313 3.313C2.4397 4.1897 2.002 5.252 2 6.5C1.998 7.748 2.4357 8.8107 3.313 9.688C4.1903 10.5653 5.2527 11.0027 6.5 11Z"
                        fill="currentColor" />
                </svg>
                <input type="text" placeholder="Search for anything"
                    class="bg-transparent border-none outline-none text-sm text-black placeholder-[#404359]/50 w-full" />
            </div>
        </div>

        <!-- Language, notification & dark mode container -->
        <div class="bg-white p-3 h-full flex items-center gap-3 rounded-full">
            <!-- Language dropdown -->
            <div class="relative">
                <button onclick="this.nextElementSibling.classList.toggle('hidden')"
                    class="flex items-center gap-1 cursor-pointer select-none">
                    <span class="text-sm font-medium text-[#404359]">EN</span>
                    <i data-lucide="chevron-down" class="w-4 h-4 text-[#404359]/60 stroke-[2.5]"></i>
                </button>
                <div class="hidden absolute top-full left-0 mt-2 bg-white rounded-2xl shadow-lg py-1 min-w-[80px] z-50">
                    <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50">EN</a>
                    <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50">FR</a>
                    <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50">AR</a>
                </div>
            </div>

            <!-- Bell -->
            <button class="cursor-pointer">
                <i data-lucide="bell" class="w-5 h-5 text-black stroke-[1.75]"></i>
            </button>

            <!-- Dark mode -->
            <button class="cursor-pointer">
                <i data-lucide="moon" class="w-5 h-5 text-black stroke-[1.75] scale-x-[-1]"></i>
            </button>

            <!-- Logout button -->
            <form method="POST" action="logout" class="m-0">
                <input type="hidden" name="type" value="logout">
                <button type="submit" class="cursor-pointer text-red-500 hover:text-red-600">
                    <i data-lucide="log-out" class="w-5 h-5"></i>
                </button>
            </form>
        </div>

        <!-- Navigation bar -->
        <div class="fixed top-[14px] left-1/2 -translate-x-1/2 z-50">
            <div class="nav-pill shadow-xl" id="navPill">
                <div class="nav-highlight" id="highlight"></div>
                <div class="nav-item active" data-page="home">
                    <i data-lucide="house"></i><span>Home</span>
                </div>
                <div class="nav-item" data-page="workspace">
                    <i data-lucide="pencil"></i><span>Workspace</span>
                </div>
                <div class="nav-item" data-page="chat">
                    <i data-lucide="message-square"></i><span>Chat</span>
                </div>
                <div class="nav-item" data-page="library">
                    <i data-lucide="library"></i><span>Library</span>
                </div>
            </div>
        </div>
    </div>

    <!-- Main content area where pages will be loaded -->
    <main id="main-content">
        <div class="container mx-auto px-6 py-8">
            <div class="text-center text-gray-500">Loading...</div>
        </div>
    </main>

    <!-- Loading overlay -->
    <div class="loading-overlay" id="loadingOverlay">
        <div class="loader"></div>
    </div>

    <script>
        // Initialize Lucide icons
        lucide.createIcons();

        // Close dropdown when clicking outside
        document.addEventListener('click', e => {
            if (!e.target.closest('.relative')) {
                document.querySelectorAll('.relative > div').forEach(d => d.classList.add('hidden'));
            }
        });

        // SPA Navigation System
        class SPANavigation {
            constructor() {
                this.items = document.querySelectorAll('.nav-item');
                this.highlight = document.getElementById('highlight');
                this.mainContent = document.getElementById('main-content');
                this.loadingOverlay = document.getElementById('loadingOverlay');
                this.currentPage = null;

                this.init();
            }

            init() {
                // Set up event listeners
                this.items.forEach(item => {
                    item.addEventListener('click', (e) => {
                        e.preventDefault();
                        const page = item.dataset.page;
                        if (page && page !== this.currentPage) {
                            this.loadPage(page, item);
                        }
                    });
                });

                // Load initial page (home)
                const activeItem = document.querySelector('.nav-item.active');
                const initialPage = activeItem ? activeItem.dataset.page : 'home';
                this.loadPage(initialPage, activeItem, false);

                // Handle browser back/forward buttons
                window.addEventListener('popstate', (event) => {
                    if (event.state && event.state.page) {
                        this.loadPage(event.state.page, null, false);
                    }
                });
            }

            moveHighlight(el) {
                if (el && this.highlight) {
                    this.highlight.style.left = el.offsetLeft + 'px';
                    this.highlight.style.width = el.offsetWidth + 'px';
                }
            }

            showLoading() {
                if (this.loadingOverlay) {
                    this.loadingOverlay.classList.add('active');
                }
            }

            hideLoading() {
                if (this.loadingOverlay) {
                    this.loadingOverlay.classList.remove('active');
                }
            }


            async loadPage(page, clickedItem, pushState = true) {
                this.showLoading();

                try {
                    // Fetch the page content using query parameters
                    const response = await fetch(`index.php?action=home&page=${page}`, {
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest'
                        }
                    });

                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }

                    const html = await response.text();

                    // Check if response is HTML (not a redirect)
                    if (html.includes('<!DOCTYPE html>') || html.includes('<html')) {
                        // This means we got a full page (probably redirect to login)
                        window.location.href = 'index.php?action=login';
                        return;
                    }

                    // Update main content with animation
                    this.mainContent.style.opacity = '0';

                    setTimeout(() => {
                        this.mainContent.innerHTML = `<div class="page-wrapper">${html}</div>`;
                        this.mainContent.style.opacity = '1';

                        // Reinitialize Lucide icons for new content
                        lucide.createIcons();

                        // Hide loading indicator
                        this.hideLoading();

                        // Update active state
                        if (clickedItem) {
                            this.items.forEach(i => i.classList.remove('active'));
                            clickedItem.classList.add('active');
                            this.moveHighlight(clickedItem);
                            this.currentPage = page;
                        } else {
                            const activeItem = document.querySelector(`.nav-item[data-page="${page}"]`);
                            if (activeItem) {
                                this.items.forEach(i => i.classList.remove('active'));
                                activeItem.classList.add('active');
                                this.moveHighlight(activeItem);
                                this.currentPage = page;
                            }
                        }

                        // Update browser history
                        if (pushState) {
                            const state = { page: page };
                            const url = `index.php?action=home&page=${page}`;
                            history.pushState(state, '', url);
                        }

                        this.initPageScripts();
                    }, 200);

                } catch (error) {
                    console.error('Error loading page:', error);
                    this.mainContent.innerHTML = `
            <div class="container mx-auto px-6 py-8">
                <div class="error-message bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded">
                    <p class="font-bold">Error loading page</p>
                    <p>Please try again or refresh the page.</p>
                </div>
            </div>
        `;
                    this.hideLoading();
                }
            }

            initPageScripts() {
                // Dispatch event for page-specific scripts
                const event = new CustomEvent('pageLoaded', {
                    detail: { page: this.currentPage }
                });
                document.dispatchEvent(event);
            }
        }

        // Initialize SPA Navigation when DOM is ready
        document.addEventListener('DOMContentLoaded', () => {
            window.spaNav = new SPANavigation();
        });

        // Re-initialize highlight on window resize
        let resizeTimeout;
        window.addEventListener('resize', () => {
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(() => {
                if (window.spaNav) {
                    const activeItem = document.querySelector('.nav-item.active');
                    if (activeItem) {
                        window.spaNav.moveHighlight(activeItem);
                    }
                }
            }, 100);
        });
    </script>
</body>

</html>