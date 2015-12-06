#ifdef _WIN32
#include "WindowsStuff.h"
#include "Seilschaften.h"

void AddWindowsConsole()
{
	AllocConsole();
	freopen("CON", "w", stdout);
	freopen("CON", "w", stderr);
	freopen("CON", "r", stdin);
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	AddWindowsConsole();
	main();
}

#endif