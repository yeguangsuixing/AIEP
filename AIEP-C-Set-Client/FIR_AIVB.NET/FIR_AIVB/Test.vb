
Imports AICS


Module Test
    Public Sub Main()
        'MsgBox("The Main procedure is  starting the application.")
        Dim ai = New MyFirAI()
        Dim loader = New FIRLoader(ai)
        loader.setHostIp("127.0.0.1")
        loader.setHostPort(8890)
        loader.execute()
        Console.ReadLine()
    End Sub
End Module
