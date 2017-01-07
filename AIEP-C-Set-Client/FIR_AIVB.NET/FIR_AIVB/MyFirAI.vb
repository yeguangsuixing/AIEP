
Imports AICS


Namespace AICS

    Public Class MyFirAI
        Implements FIR_AI

        Public Function getId() As String Implements AI.getId
            Return "b101220005我们"
        End Function

        Public Function getName() As String Implements AI.getName
            Return "FFFF"
        End Function

        Public Function getNickname() As String Implements AI.getNickname
            Return "ffff"
        End Function

        Public Function getVersion() As String Implements AI.getVersion
            Return "v0.1"
        End Function

        Public Function isPrintInfo() As Boolean Implements AI.isPrintInfo
            Return True
        End Function

        Public Function getLastGameTree() As [GameTree] Implements FIR_AI.getLastGameTree
            Return Nothing
        End Function

        Public Function itsmyturn(ByVal opponentStep As [Step]) As [Step] Implements FIR_AI.itsmyturn
            Console.WriteLine("""itsmyturn"" is called by the system.")
            Dim x = opponentStep.getX()
            Dim y = opponentStep.getY()
            Return New [Step](Status.EMPTY, x + 1, y)
        End Function

        Public Function itsmyturn2(ByVal opponentStep As [Step]) As [Step] Implements FIR_AI.itsmyturn2
            Return itsmyturn(opponentStep)
        End Function

        Public Sub setInningInfo(ByVal myStatus As [Status], ByVal limitedTime As Integer, ByVal opponentInfo As [StudentInfo], ByVal piecesArray() As [Status]) Implements FIR_AI.setInningInfo
            Console.WriteLine("""setInningInfo"" is called by the system.")
        End Sub

        
    End Class

End Namespace