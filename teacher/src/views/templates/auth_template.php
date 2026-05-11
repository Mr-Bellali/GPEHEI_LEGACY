<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
</head>

<body class="w-full h-screen p-6 bg-[#3D348B] flex">
    <!-- Left side that will contain logo and general infos -->
    <div class="w-1/2 flex flex-col items-start justify-between h-full">
        <h1 class="font-medium text-white text-3xl">
            EHEI
        </h1>
        <div class="w-full h-full flex justify-center items-center">
            <p class="text-[500px] text-[#7678ED]/50">
                L
            </p>
        </div>
        <p class="text-[#D1D1D1] text-xs font-medium">
            @G321 2026 All rights reserved
        </p>
    </div>
    <!-- Right side: it will hold different forms based on the user needs -->
    <div class="w-1/2 h-full bg-[#7678ED] rounded-[10px] px-6 pb-6 pt-[15%]">
        <?php if (!empty($_SESSION['error'])): ?>
            <p class="text-white bg-red-500/40 text-sm px-4 py-2 rounded mb-4 absolute top-4 right-4">
                <?= htmlspecialchars($_SESSION['error']) ?>
            </p>
            <?php unset($_SESSION['error']); ?>
        <?php endif; ?>
        
        <?php include_once("views/forms/login_form.php"); ?>
    </div>

</body>

</html>