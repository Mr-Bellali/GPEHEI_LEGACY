<div class="mx-auto flex flex-col gap-6">
    <!-- Filters section -->
    <div class="w-full h-fit flex">
        <!-- Select the filiere -->
        <div class="relative">
            <button onclick="this.nextElementSibling.classList.toggle('hidden')"
                class="flex items-center gap-1 cursor-pointer select-none h-[50px] bg-white p-3 rounded-full">
                <span id="selectedFiliereName" class="text-sm font-medium text-[#404359]">
                    <?php echo htmlspecialchars($selectedFiliere->name_filier ?? 'Select Filiere'); ?>
                </span>
                <i data-lucide="chevron-down" class="w-4 h-4 text-[#404359]/60 stroke-[2.5]"></i>
            </button>
            <div class="hidden absolute top-full left-0 mt-2 bg-white rounded-2xl shadow-lg py-1 min-w-[200px] z-50">
                <?php if (!empty($filieres)): ?>
                    <?php foreach ($filieres as $filiere): ?>
                        <a href="#" onclick="loadFiliere(<?php echo $filiere->id; ?>); return false;"
                            class="block px-4 py-2 text-sm text-[#404359] hover:bg-gray-50 rounded-full filiere-option"
                            data-id="<?php echo $filiere->id; ?>"
                            data-name="<?php echo htmlspecialchars($filiere->name_filier); ?>">
                            <?php echo htmlspecialchars($filiere->name_filier); ?>
                        </a>
                    <?php endforeach; ?>
                <?php else: ?>
                    <span class="block px-4 py-2 text-sm text-gray-500">No filieres assigned</span>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <!-- Main content - Show ONLY levels that have groups -->
    <div id="workspaceContent" class="flex flex-col gap-2">
        <?php
        // Check if groupsByLevel exists and has data
        if (isset($groupsByLevel) && !empty($groupsByLevel)) {
            // Loop through academic levels 1-5 in order
            for ($level = 1; $level <= 5; $level++) {
                // Only show the level if it has groups
                if (isset($groupsByLevel[$level]) && $groupsByLevel[$level]['total_groups'] > 0) {
                    $levelData = $groupsByLevel[$level];
                    ?>
                    <!-- <?php echo $levelData['name']; ?> -->
                    <div class="mb-6">
                        <!-- Year's title -->
                        <div class="w-full flex flex-col gap-1 mb-2">
                            <h1 class="font-bold text-xl text-[#3D348B]">
                                <?php echo htmlspecialchars($levelData['name']); ?>
                            </h1>
                            <hr class="border-[#3D348B]">
                        </div>

                        <!-- A grid layout of all the classes -->
                        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                            <?php foreach ($levelData['groups'] as $group): ?>
                                <!-- A card for the group -->
                                <div class="flex flex-col px-2 py-1 bg-[#7678ED] rounded-[10px] w-full min-w-[300px] gap-4 hover:shadow-lg transition-shadow cursor-pointer"
                                    onclick="window.location.href='index.php?action=group&id=<?php echo $group->id; ?>'">
                                    <!-- Group's name -->
                                    <h2 class="text-xl text-white font-semibold">
                                        Group <?php echo htmlspecialchars($group->group_name); ?>
                                    </h2>
                                    <!-- Basic group's info -->
                                    <div class="flex items-center justify-between text-white text-xs">
                                        <p><?php echo htmlspecialchars($group->filiere_name); ?></p>
                                        <p class="flex items-center gap-1">
                                            <span><i data-lucide="users" class="w-4 h-4"></i></span>
                                            <?php echo $group->student_count; ?>
                                        </p>
                                    </div>
                                </div>
                            <?php endforeach; ?>
                        </div>
                    </div>
                    <?php
                }
            }
        } else {
            // No groups at all
            echo '<div class="text-center py-12">
                    <i data-lucide="inbox" class="w-16 h-16 mx-auto text-gray-400 mb-4"></i>
                    <h3 class="text-lg font-medium text-gray-900">No groups found</h3>
                    <p class="text-gray-500 mt-1">No groups available for this filiere.</p>
                  </div>';
        }
        ?>
    </div>

    <div></div>
</div>

<script>
    // Function to load filiere content without page reload
    function loadFiliere(filiereId) {
        // Get the selected filiere name from the clicked link
        const selectedLink = event.target.closest('.filiere-option');
        const selectedName = selectedLink ? selectedLink.getAttribute('data-name') : '';

        // Update the button text
        document.getElementById('selectedFiliereName').innerText = selectedName;

        // Close the dropdown
        const dropdownMenu = document.querySelector('.relative button + div');
        if (dropdownMenu) dropdownMenu.classList.add('hidden');

        // Show loading state
        const contentDiv = document.getElementById('workspaceContent');
        contentDiv.innerHTML = `
        <div class="text-center py-12">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-[#3D348B]"></div>
            <p class="mt-2 text-gray-500">Loading...</p>
        </div>
    `;

        // Fetch the new content
        fetch(`index.php?action=workspace&filiere_id=${filiereId}&ajax=1`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                // Update content
                contentDiv.innerHTML = data.content;

                // Update promotion info
                const promotionDiv = document.getElementById('promotionInfo');
                if (promotionDiv && data.promotionInfo) {
                    promotionDiv.innerHTML = data.promotionInfo;
                }

                // Re-initialize Lucide icons
                if (typeof lucide !== 'undefined') {
                    lucide.createIcons();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                contentDiv.innerHTML = `
            <div class="text-center py-12">
                <i data-lucide="alert-circle" class="w-16 h-16 mx-auto text-red-500 mb-4"></i>
                <h3 class="text-lg font-medium text-gray-900">Error loading content</h3>
                <p class="text-gray-500 mt-1">Please try again.</p>
            </div>
        `;
                if (typeof lucide !== 'undefined') {
                    lucide.createIcons();
                }
            });
    }

    // Close dropdown when clicking outside
    document.addEventListener('click', function (event) {
        const dropdown = document.querySelector('.relative');
        const button = dropdown?.querySelector('button');
        const menu = dropdown?.querySelector('button + div');

        if (dropdown && button && menu && !dropdown.contains(event.target)) {
            menu.classList.add('hidden');
        }
    });

    // Re-initialize Lucide icons after content loads
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
</script>