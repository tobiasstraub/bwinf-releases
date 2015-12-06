#include "Application.h"

#include <Windows.h>
#include <io.h>
#include <fcntl.h>
#include <stdio.h>

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	AllocConsole();
	char* stdobuf = new char[4096];
	setvbuf(stdout, stdobuf, _IOFBF, 4096);
	freopen("CONOUT$", "w", stdout);
	freopen("CONOUT$", "w", stderr);
	freopen("CONIN$", "r", stdin);

	Application a;
	a.run();
	return 0;
}
