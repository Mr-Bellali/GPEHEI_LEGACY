<?php
// Workspace logic here
?>
<div class="container mx-auto px-6 py-8">
    <h1 class="text-3xl font-bold text-[#3D348B] mb-6">Your Workspace</h1>
    <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-semibold text-gray-800">Projects</h2>
            <button class="bg-[#3D348B] text-white px-4 py-2 rounded-lg hover:bg-[#2d2668] transition-colors">
                <i data-lucide="plus" class="w-4 h-4 inline mr-2"></i>
                New Project
            </button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- Project cards would go here -->
            <div class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <h3 class="font-semibold text-gray-800">Project Alpha</h3>
                <p class="text-sm text-gray-500 mt-1">Last edited 2 days ago</p>
                <div class="mt-3 flex gap-2">
                    <span class="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded">In Progress</span>
                </div>
            </div>
            <div class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <h3 class="font-semibold text-gray-800">Project Beta</h3>
                <p class="text-sm text-gray-500 mt-1">Last edited 5 days ago</p>
                <div class="mt-3 flex gap-2">
                    <span class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Planning</span>
                </div>
            </div>
        </div>
    </div>
</div>