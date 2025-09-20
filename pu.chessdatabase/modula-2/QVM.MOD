MODULE QVM;
IMPORT VM;
(*
*******************************************************************************
Testen van module VM
*******************************************************************************
*)


PROCEDURE Qcreate();
BEGIN
    VM.Create("KDKT.dbs"); (* ok *)
    VM.Close();            (* ok *)
END Qcreate;


BEGIN
    Qcreate();
END QVM.