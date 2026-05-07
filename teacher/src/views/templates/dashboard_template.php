<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <script src="https://unpkg.com/lucide@latest"></script>
    <title>EHEI</title>

    <style>
        .nav-pill {
            background: #3D348B;
            border-radius: 9999px;
            padding: 6px;
            display: inline-flex;
            align-items: center;
            gap: 2px;
            position: relative;
            /* needed for highlight */
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

        /* Page transitions */
        .page {
            display: none;
        }

        .page.active {
            display: block;
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
    </style>

</head>

<body class="w-full h-screen bg-[#F0F0F0] px-6 pt-[14px]">
    <!-- Header -->
    <div class="w-full h-[50px] flex justify-between">
        <!-- Logo & search input container -->
        <div class="h-full flex gap-4">
            <!-- Logo placeholder -->
            <div class="w-[50px] h-[50px] bg-white">
                <!-- Logo to be added later -->
            </div>

            <!-- Search input here -->
            <div class="flex items-center bg-white rounded-full px-5 w-60 h-[50px] gap-2.5">
                <!-- Search icon -->
                <svg class="shrink-0 text-[#404359]/50" width="18" height="18" viewBox="0 0 18 18" fill="none"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                        d="M16.6 18L10.3 11.7C9.8 12.1 9.225 12.4167 8.575 12.65C7.925 12.8833 7.2333 13 6.5 13C4.6833 13 3.146 12.3707 1.888 11.112C0.63 9.8533 0.0007 8.316 0 6.5C-0.0007 4.684 0.6287 3.1467 1.888 1.888C3.1473 0.6293 4.6847 0 6.5 0C8.3153 0 9.853 0.6293 11.113 1.888C12.373 3.1467 13.002 4.684 13 6.5C13 7.2333 12.8833 7.925 12.65 8.575C12.4167 9.225 12.1 9.8 11.7 10.3L18 16.6L16.6 18ZM6.5 11C7.75 11 8.8127 10.5627 9.688 9.688C10.5633 8.8133 11.0007 7.7507 11 6.5C10.9993 5.2493 10.562 4.187 9.688 3.313C8.814 2.439 7.7513 2.0013 6.5 2C5.2487 1.9987 4.1863 2.4363 3.313 3.313C2.4397 4.1897 2.002 5.252 2 6.5C1.998 7.748 2.4357 8.8107 3.313 9.688C4.1903 10.5653 5.2527 11.0027 6.5 11Z"
                        fill="currentColor" />
                </svg>

                <!-- Input -->
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

                <!-- Dropdown menu -->
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

        </div>

        <!-- Navigation bar -->
        <div class="fixed top-[14px] left-1/2 -translate-x-1/2 z-50">
            <div class="nav-pill shadow-xl" id="navPill">
                <div class="nav-highlight" id="highlight"></div>
                <div class="nav-item active" data-page="home"><i data-lucide="house"></i><span>Home</span></div>
                <div class="nav-item" data-page="workspace"><i data-lucide="pencil"></i><span>Workspace</span></div>
                <div class="nav-item" data-page="chat"><i data-lucide="message-square"></i><span>Chat</span></div>
                <div class="nav-item" data-page="library"><i data-lucide="library"></i><span>Library</span></div>
            </div>
        </div>
    </div>

    <script>
        lucide.createIcons();

        // Close dropdown when clicking outside
        document.addEventListener('click', e => {
            if (!e.target.closest('.relative')) {
                document.querySelectorAll('.relative > div').forEach(d => d.classList.add('hidden'));
            }
        });

        // -----------------------------------
        const items = document.querySelectorAll('.nav-item');
        const highlight = document.getElementById('highlight');

        function moveHighlight(el) {
            highlight.style.left = el.offsetLeft + 'px';
            highlight.style.width = el.offsetWidth + 'px';
        }

        window.addEventListener('load', () => moveHighlight(document.querySelector('.nav-item.active')));

        items.forEach(item => item.addEventListener('click', () => {
            items.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            moveHighlight(item);
            document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
            document.getElementById('page-' + item.dataset.page).classList.add('active');
        }));
    </script>
</body>

</html>