#!/bin/bash

# Install X11 and OpenJDK with GUI support
sudo apt-get update
sudo apt-get install -y x11-apps openjdk-11-jdk

# Get Windows IP address
export WINDOWS_IP=$(ip route | grep default | awk '{print $3}')

# Add X11 display export to bashrc if not already present
if ! grep -q "export DISPLAY" ~/.bashrc; then
    echo "export DISPLAY=$WINDOWS_IP:0.0" >> ~/.bashrc
    echo "export LIBGL_ALWAYS_INDIRECT=1" >> ~/.bashrc
fi

# Set display for current session
export DISPLAY=$WINDOWS_IP:0.0
export LIBGL_ALWAYS_INDIRECT=1

echo "Please do the following steps:"
echo "1. Install VcXsrv on Windows from: https://sourceforge.net/projects/vcxsrv/"
echo "2. Run XLaunch and configure with these settings:"
echo "   - Multiple windows"
echo "   - Display number: 0"
echo "   - Start no client"
echo "   - Extra settings: Check 'Disable access control'"
echo "   - Save configuration for next time"
echo "3. In Windows Defender Firewall:"
echo "   - Allow VcXsrv through the firewall"
echo "   - Enable both private and public networks"
echo "4. Restart your WSL terminal"
echo "5. Test with: xclock"
echo "6. If xclock works, run: ./build.sh"
