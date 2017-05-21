@echo off
echo Compiling...
javac ..\pc-app\MakeMe.java -d .
echo Creating directory C:\Users\%USERNAME%\AppData\Local\MakeMe
mkdir C:\Users\%USERNAME%\AppData\Local\MakeMe
echo Copying classes...
copy MakeMe.class C:\Users\%USERNAME%\AppData\Local\MakeMe\MakeMe.class
copy Poziv.class C:\Users\%USERNAME%\AppData\Local\MakeMe\Poziv.class
copy Blokada.class C:\Users\%USERNAME%\AppData\Local\MakeMe\Blokada.class
echo Creating Startup entry...
copy run-make-me.bat "C:\Users\%USERNAME%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup"
echo Removing Class files...
del MakeMe.class
del Poziv.class
del Blokada.class
echo Done!
pause