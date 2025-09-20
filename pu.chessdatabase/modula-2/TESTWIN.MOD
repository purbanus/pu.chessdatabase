MODULE TestWin;

IMPORT Window, IO;

CONST
	CacheFG=Window.White;
	CacheBG=Window.Red  ;
	
VAR
	W : Window.WinType;
	
BEGIN
    W:= Window.Open(Window.WinDef(   0, 0, 10, 10,
                       CacheFG, CacheBG,
                  (*---CursorOn WrapOn Hidden FrameOn---*)
                       FALSE,   FALSE, FALSE, FALSE,
                       Window.SingleFrame, CacheFG, CacheBG));
    Window.GotoXY( 1, 1); IO.WrChar('a');
    Window.GotoXY(11, 1); IO.WrChar('b');
    Window.GotoXY( 1,11); IO.WrChar('c');
    Window.GotoXY(11,11); IO.WrChar('d');
END TestWin.
