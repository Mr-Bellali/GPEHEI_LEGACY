<?php
// Workspace logic here
?>
<div class="mx-auto flex flex-col gap-6">
    <!-- Filters section -->
    <div class="w-full h-fit flex">
        <!-- Select the filiere later will be seeded with all the filieres in database related to the teacher -->
        <div class="relative">
            <button onclick="this.nextElementSibling.classList.toggle('hidden')"
                class="flex items-center gap-1 cursor-pointer select-none h-[50px] bg-white p-3 rounded-full">
                <span class="text-sm font-medium text-[#404359]">Ginie informatique</span>
                <i data-lucide="chevron-down" class="w-4 h-4 text-[#404359]/60 stroke-[2.5]"></i>
            </button>
            <div class="hidden absolute top-full left-0 mt-2 bg-white rounded-2xl shadow-lg py-1 min-w-[80px] z-50">
                <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50 rounded-full">Ginie
                    informatique</a>
                <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50 rounded-full">Informatique et
                    gestion</a>
                <a href="#" class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50 rounded-full">Ginie
                    industriel</a>
            </div>
        </div>

    </div>

    <!-- Main content -->
    <div class="flex flex-col gap-2">
        <!-- First year -->
        <div class="mb-6">
            <!-- Year's title -->
            <div class="w-full flex flex-col gap-1 mb-2">
                <h1 class="font-bold text-xl text-[#3D348B]">
                    1st year
                </h1>
                <hr class="border-[#3D348B]">
            </div>

            <!-- A grid layout of all the classes -->
            <div class="grid grid-cols-5 gap-4">
                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group A
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group B
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group C
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group D
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group E
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group F
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

            </div>
        </div>

        <!-- Second year -->
        <div>
            <!-- Year's title -->
            <div class="w-full flex flex-col gap-1 mb-2">
                <h1 class="font-bold text-xl text-[#3D348B]">
                    2nd year
                </h1>
                <hr class="border-[#3D348B]">
            </div>

            <!-- A grid layout of all the classes -->
            <div class="grid grid-cols-5 gap-4">
                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group A
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group B
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group C
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group D
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group E
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

                <!-- A card for the group -->
                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-fit min-w-[300px] gap-4">
                    <!-- Group's name -->
                    <h2 class="text-xl text-white">
                        Group F
                    </h2>
                    <!-- Basic group's info -->
                    <div class="flex items-center justify-between text-white text-xs">
                        <p>G.I</p>
                        <p class="flex items-center gap-1"><span><i data-lucide="users" class="w-4 h-4"></i></span>30
                        </p>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <div></div>
</div>