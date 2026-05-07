<?php
// You can include any PHP logic here
// Access session data if needed
$user_id = $_SESSION['user_id'] ?? null;
?>
<div class="container mx-auto px-6 py-8">
    <div class="mb-8">
        <h1 class="text-3xl font-bold text-[#3D348B] mb-2">Welcome Back!</h1>
        <p class="text-gray-600">Here's what's happening with your projects today.</p>
    </div>
    
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- Statistics Cards -->
        <div class="bg-white rounded-lg p-6 shadow-md hover:shadow-lg transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-700">Active Projects</h3>
                <i data-lucide="folder-open" class="w-6 h-6 text-[#3D348B]"></i>
            </div>
            <p class="text-3xl font-bold text-[#3D348B]">12</p>
            <p class="text-sm text-gray-500 mt-2">+2 from last month</p>
        </div>
        
        <div class="bg-white rounded-lg p-6 shadow-md hover:shadow-lg transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-700">Messages</h3>
                <i data-lucide="message-circle" class="w-6 h-6 text-[#3D348B]"></i>
            </div>
            <p class="text-3xl font-bold text-[#3D348B]">8</p>
            <p class="text-sm text-gray-500 mt-2">3 unread messages</p>
        </div>
        
        <div class="bg-white rounded-lg p-6 shadow-md hover:shadow-lg transition-shadow">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold text-gray-700">Documents</h3>
                <i data-lucide="file-text" class="w-6 h-6 text-[#3D348B]"></i>
            </div>
            <p class="text-3xl font-bold text-[#3D348B]">24</p>
            <p class="text-sm text-gray-500 mt-2">5 shared this week</p>
        </div>
    </div>
    
    <!-- Recent Activity -->
    <div class="mt-8 bg-white rounded-lg shadow-md p-6">
        <h2 class="text-xl font-bold text-gray-800 mb-4">Recent Activity</h2>
        <div class="space-y-3">
            <div class="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div class="w-2 h-2 bg-green-500 rounded-full"></div>
                <p class="text-gray-600">Project "Website Redesign" was updated</p>
                <span class="text-sm text-gray-400 ml-auto">2 hours ago</span>
            </div>
            <div class="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
                <p class="text-gray-600">New comment on your workspace</p>
                <span class="text-sm text-gray-400 ml-auto">5 hours ago</span>
            </div>
            <div class="flex items-center gap-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
                <div class="w-2 h-2 bg-purple-500 rounded-full"></div>
                <p class="text-gray-600">You added a new document to Library</p>
                <span class="text-sm text-gray-400 ml-auto">Yesterday</span>
            </div>
        </div>
    </div>
</div>