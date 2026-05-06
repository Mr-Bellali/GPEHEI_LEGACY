<form action="" class="w-full h-full flex flex-col justify-between">
    <!-- Title & fields container -->
    <div class="flex flex-col gap-[50px] ">
        <h1 class="text-[64px] text-white">
            Login
        </h1>
        <!-- Fields -->
        <div class="flex flex-col gap-6">
            <div class="flex gap-6">
                <!-- Email -->
                <div class="flex-1 flex flex-col gap-1">
                    <label class="text-white/90 text-sm">Email</label>
                    <div class="relative flex items-center">
                        <input type="email" name="email" placeholder="xyz@example.com"
                            class="w-full bg-transparent border-b border-white/50 py-2 pr-7 text-sm text-white/70 placeholder-white/50 outline-none focus:border-white" />
                    </div>
                </div>

                <!-- Password -->
                <div class="flex-1 flex flex-col gap-1">
                    <label class="text-white/90 text-sm">Password</label>
                    <div class="relative flex items-center">
                        <input type="password" id="pwd" name="password" placeholder="Your password is here"
                            class="w-full bg-transparent border-b border-white/50 py-2 pr-7 text-sm text-white/70 placeholder-white/50 outline-none focus:border-white" />
                        <button type="button" id="togglePwd"
                            class="absolute right-0 text-white/70 hover:text-white flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none"
                                viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
                                <path stroke-linecap="round" stroke-linejoin="round"
                                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                <circle cx="12" cy="12" r="3" />
                            </svg>
                        </button>
                    </div>
                </div>

            </div>

            <!-- Remember me -->
            <label for="rememberMe" class="flex items-center gap-1 cursor-pointer select-none">
                <input type="checkbox" id="rememberMe" name="remember" class="hidden" />
                <!-- Icon wrapper -->
                <div class="relative w-5 h-5 text-white/80">
                    <!-- unchecked -->
                    <i id="uncheckedIcon" class="fa-regular fa-square absolute inset-0"></i>
                    <!-- checked -->
                    <i id="checkedIcon" class="fa-solid fa-square-check absolute inset-0 hidden"></i>
                </div>
                <span class="text-white/90 text-sm">Remember me</span>
            </label>

        </div>

    </div>
    <!-- Actions -->
    <div class="flex flex-row items-end justify-between">
        <button type="button" class="text-sm text-white/50 hover:text-white cursor-pointer ">
            Forgot password?
        </button>

        <button type="submit" class="text-3xl text-[#3D348B] bg-white cursor-pointer px-[34px] py-[10px] hover:bg-[#D1D1D1] rounded-[5px]">
            Sign in
        </button>
    </div>
</form>

<script>
    document.getElementById('togglePwd').addEventListener('click', function () {
        const p = document.getElementById('pwd');
        p.type = p.type === 'password' ? 'text' : 'password';
    });

    // Remember me toggle
    const rememberMe = document.getElementById('rememberMe');
    const uncheckedIcon = document.getElementById('uncheckedIcon');
    const checkedIcon = document.getElementById('checkedIcon');
    
    rememberMe.addEventListener('change', function() {
        if (this.checked) {
            uncheckedIcon.classList.add('hidden');
            checkedIcon.classList.remove('hidden');
        } else {
            uncheckedIcon.classList.remove('hidden');
            checkedIcon.classList.add('hidden');
        }
    });
</script>
