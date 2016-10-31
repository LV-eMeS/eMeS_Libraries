unit eMeSCommandServerU;

interface

uses eMeSServerCoreU, eMeS_Table_Row_HandlerU, eMeS_ListU, eMeSCommandU, eMeSClientServerConstantsU,
     Classes, Sysutils, IdContext, IdSchedulerOfThread;

type
  { Nepiecie�amie juniti, lai pilnv�rt�gi izmantotu �o klasi:
    * eMeSClientServerConstantsU
    * eMeSCommandU
    * eMeS_Table_Row_HandlerU

    Izmanto, lai s�t�tu specifiskas komandas klientiem, k� ar� sa�emtu t�da pa�a form�ta komandas.
    Komandas iesp�jams �ifr�t ar iepriek�defin�tu �ifr��anas algoritmu, kas j�defin�, uzst�dot
    `EncryptionAlgorithm un `DecryptionAlgorithm, k� ar� aktiviz�jot `EncryptMessages := true;
    L�dz ar to, visas izejo��s zi�as tiks �ifr�tas izmantojot �os algoritmus.

    //�r�j�s br�v�s proced�ras
    function TestEncryptionAlgorithm(text:string):String;
    begin
      Result := '...Some kind of algorithm here to encrypt text...';
    end;
    function TestDecryptionAlgorithm(text:string):String;
    begin
      Result := '...Some kind of algorithm here to decrypt text...';
    end;
    procedure TestRegCommand(const aCmdData:TeMeS_Row; aSenderID:TUserIDType=cNoIDValue);
    //CmdData.ID ir saglab�ts komandas numurs
    begin
      //aCmdData dom�ts tikai las��anai, ja datus �rpus ��s proced�ras kkur j�izmanto,
      //tad tos j�nokop�, jo p�c tam objekts aCmdData tiek izn�cin�ts autom�tiski (tas nav proced�r� TestRegCommand pa�am j�atbr�vo)
      Showmessage('User ID = '+IntToStr(aSenderID)+#13+
        'Command data is: ' + aCmdData.AsString(' '));
    end;

    Server := TeMeSCommandServer.Create(self);
    Server.EncryptionAlgorithm := TestEncryptionAlgorithm;
    Server.DecryptionAlgorithm := TestDecryptionAlgorithm;
    Server.EncryptMessages := true;

    `MsgToAll un `MsgToClientByID princip� nevajadz�tu izmantot komandu serverim, ja vien�gi t�s nav
    dom�tas, lai vienk�r�i �ifr�tu zi�u. Princip� `ListeningMode nosaka, k� darbojas komandu serveris. Ir divi varianti:
    1) lmMessages re��ms padara to par princip� t�du pa�u serveri k� TeMeSServerCore klas� aprakst�to. Zi�as tiek s�t�tas,
       apstr�d�tas un sa�emtas glu�i t�pat. Ar� `OnIncomingCommand events tiek izpild�ts t�pat k� iepriek�.
    2) lmCommands re��ms visu apgrie� k�j�m gais�. zi�as tiek sa�emtas un izpild�tas k� komandas. `OnIncomingCommand tiek ignor�ts, jo
       visas iesp�jam�s komandas jau ir re�istr�tas iek� `CommandList saraksta. T�s ar� autom�tiski tiek izpild�tas p�c instrukcijas.
       Ja komanda nav atrasta, tad ien�ko�� zi�a vienk�r�i tiek ignor�ta.
    �im objektam �o meto�u viet� b�s `CmdToAll un `CmdToClientByID,
    kur k� parametri b�s j�padod komanda k� TeMeS_Row objekts, kura pirm� kolonna b�s komandas k�rtas numurs jeb ID, k�
    p�r�jie p�r�jie dati, kas nepiecie�ami komandas apstr�dei. Otrais parametrs ir j�nodod teksta atdal�t�j simbols, kas
    noteiks, k�ds simbols tekst� atdala vienu kolonnu no otras. P.S. TeMeS_Row pa�am ir j�rada un j�atbr�vo, metodes
    to izmanto tikai datu ieg��anai.


    Server.ListeningMode := lmCommands;
    Server.RegisterNewCommand(1, TestRegCommand);
    //(...) �eit re�istr� ar� p�r�j�s atpaz�stam�s komandas, kuras tiks s�t�tas serverim

    Server.StartServer(12345);
  }
  TeMeSCommandServer = class(TeMeSServerCore)
    private
      FEncryptMessages:Boolean;
      FEncryptionAlgorithm, FDecryptionAlgorithm:TEncryptionAlgorithm;
      FInternalCmdDataContainer:TeMeS_Row;
      FThreadSafeID:TUserIDType;
      FThreadSafeClientCmd:String;
      procedure pSetEncryptMessages(aValue:Boolean);
      procedure pThreadSafeProcessIncomingCommand;
      procedure pProcessIncomingCommand(aClientID:TUserIDType; aCmdText:String);
      procedure pHashMessage(var aMsgToHash: TMsgString); //@eMeS Hashing
      procedure pValidateHashedMessage(aMessageAsColumns: TeMeS_Row); //@eMeS Hashing
    protected
      procedure ServerSocketExecute(AContext: TIdContext); override;
    public
      CommandList:TeMeS_List<TeMeSCommand>;
      ListeningMode:TListeningMode;
      HashPass:TMsgString;
      constructor Create(AOwner:TComponent);
      destructor Destroy; override;
      procedure MsgToAll(Msg: TMsgString);
      procedure MsgToClientByID(Msg: TMsgString; ID: TUserIDType);
      procedure CmdToAll(const aCmdCode: TeMeS_CommandType; aUseInternalDataContainer:Boolean=False); overload;
      procedure CmdToAll(const aCmdCode: TeMeS_CommandType; const aCmdData: TeMeS_Row); overload;
      procedure CmdToClientByID(const aCmdCode: TeMeS_CommandType; aUserID: TUserIDType; aUseInternalDataContainer:Boolean=False); overload;
      procedure CmdToClientByID(const aCmdCode: TeMeS_CommandType; const aCmdData: TeMeS_Row; aUserID: TUserIDType); overload;
      procedure RegisterNewCommand(aCmdCode: TeMeS_CommandType; aProcToHandle:TCommandExecutableProcedure);
      procedure PrepareDataContainer;
      property EncryptMessages:Boolean read FEncryptMessages write pSetEncryptMessages;
      property EncryptionAlgorithm:TEncryptionAlgorithm read FEncryptionAlgorithm write FEncryptionAlgorithm;
      property DecryptionAlgorithm:TEncryptionAlgorithm read FDecryptionAlgorithm write FDecryptionAlgorithm;
      property InternalCmdDataContainer:TeMeS_Row read FInternalCmdDataContainer;
      property DataContainer:TeMeS_Row read FInternalCmdDataContainer; //alias
      property CmdDataContainer:TeMeS_Row read FInternalCmdDataContainer; //alias
  end;

implementation

{ TeMeSCommandServer }

uses eMeS_HashFunctionU;

procedure TeMeSCommandServer.CmdToAll(const aCmdCode: TeMeS_CommandType; const aCmdData: TeMeS_Row);
begin
  aCmdData.AddColumn(aCmdCode);
  MsgToAll(aCmdData.AsString);
end;

procedure TeMeSCommandServer.CmdToAll(const aCmdCode: TeMeS_CommandType; aUseInternalDataContainer:Boolean=False);
begin
  if aUseInternalDataContainer then
    CmdToAll(aCmdCode, FInternalCmdDataContainer)
  else
    MsgToAll(aCmdCode+cDefaultDelim);
end;

procedure TeMeSCommandServer.CmdToClientByID(const aCmdCode: TeMeS_CommandType; const aCmdData: TeMeS_Row; aUserID: TUserIDType);
begin
  aCmdData.AddColumn(aCmdCode); //komandas kods b�s pats p�d�jais virknes elements
  MsgToClientByID(aCmdData.AsString, aUserID);
end;

procedure TeMeSCommandServer.CmdToClientByID(const aCmdCode: TeMeS_CommandType; aUserID: TUserIDType; aUseInternalDataContainer:Boolean=False);
begin
  if aUseInternalDataContainer then
    CmdToClientByID(aCmdCode, FInternalCmdDataContainer, aUserID)
  else
    MsgToClientByID(aCmdCode+cDefaultDelim, aUserID);
end;

procedure TeMeSCommandServer.PrepareDataContainer;
begin
  FInternalCmdDataContainer.EraseData;
end;

constructor TeMeSCommandServer.Create(AOwner: TComponent);
begin
  inherited;
  FEncryptMessages := false;
  EncryptionAlgorithm := nil;
  CommandList := TeMeS_List<TeMeSCommand>.Create(true);
  ListeningMode := lmCommands;
  FInternalCmdDataContainer := TeMeS_Row.Create;
end;

destructor TeMeSCommandServer.Destroy;
begin
  FInternalCmdDataContainer.Free;
  CommandList.Free;
  inherited;
end;

procedure TeMeSCommandServer.MsgToAll(Msg: TMsgString);
begin
  pHashMessage(Msg); //ja nepiecie�ama ha���ana, dar�sim to! @eMeS Hashing
  if self.FEncryptMessages then
    Msg := self.FEncryptionAlgorithm(Msg);
  inherited;
end;

procedure TeMeSCommandServer.MsgToClientByID(Msg: TMsgString; ID: TUserIDType);
begin
  pHashMessage(Msg); //ja nepiecie�ama ha���ana, dar�sim to! @eMeS Hashing
  if self.FEncryptMessages then
    Msg := self.FEncryptionAlgorithm(Msg);
  inherited;
end;

procedure TeMeSCommandServer.pHashMessage(var aMsgToHash: TMsgString);
var FullHashKey:TMsgString; //@eMeS Hashing
begin
  if HashPass <> '' then
  begin
    FullHashKey := eMeSHashFromString(aMsgToHash+HashPass);
    aMsgToHash := aMsgToHash + FullHashKey + cDefaultDelim;
  end;
end;

procedure TeMeSCommandServer.pProcessIncomingCommand(aClientID: TUserIDType; aCmdText: String);
var CmdData:TeMeS_Row; aCmdCode:TeMeS_CommandType;
    FoundCommand:Boolean;
begin
  CmdData := TeMeS_Row.Create;
  CmdData.MakeFromString(aCmdText, cDefaultDelim);

  try
    if self.HashPass <> '' then
      pValidateHashedMessage(CmdData); //�is izmet�s exception, ja neb�s kkas labi ar ha���anu //@eMeS Hashing
    aCmdCode := CmdData.GetColumn(CmdData.Count); //p�d�j� komanda ir komandas kods, p�c kura atpaz�sim komandas
  except //fake komandas pat neapskat�sim
    CmdData.Free;
    exit;
  end;

  FoundCommand := false;
  self.CommandList.First;
  while CommandList.NotAtTheEndOfList do
  begin
    FoundCommand := CommandList.Current.Code = aCmdCode;
    if FoundCommand then Break;
    CommandList.Next;
  end;

  if FoundCommand then
  begin
    CmdData.RemoveColumn(CmdData.Count);
    CommandList.Current.DoOnReceiveThisCommand(CmdData, aClientID);
  end;

  CmdData.Free;
end;

procedure TeMeSCommandServer.RegisterNewCommand(aCmdCode: TeMeS_CommandType; aProcToHandle: TCommandExecutableProcedure);
var newCmd:TeMeSCommand;
begin
  newCmd := TeMeSCommand.Create;
  newCmd.Code := aCmdCode;
  newCmd.DoOnReceiveThisCommand := aProcToHandle;
  self.CommandList.Add(newCmd);
end;

procedure TeMeSCommandServer.ServerSocketExecute(AContext: TIdContext);
var
  ID:TUserIDType;
  ClientCmd:String;
begin
  if ListeningMode = lmCommands then //ja klaus�mies komandas
  begin
    ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
    ClientCmd := pReadlnMessage(AContext);
    if self.FEncryptMessages then //ar� komandas iesp�jams izv�l�ties �ifr�t vai n�
      ClientCmd := self.FDecryptionAlgorithm(ClientCmd);
    try
      FThreadSafeID := ID;
      FThreadSafeClientCmd := ClientCmd;
      TIdYarnOfThread(AContext.Yarn).Thread.Synchronize(pThreadSafeProcessIncomingCommand);
    finally
    end;
  end else //ja klaus�mies parastas zi�as
  begin
    if self.FEncryptMessages then
    begin
      ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
      ClientCmd := pReadlnMessage(AContext);
      ClientCmd := self.FDecryptionAlgorithm(ClientCmd);

      if Assigned(FOnExecuteAdd) then
        FOnExecuteAdd(ID, ClientCmd);
    end else
      inherited;
  end;
end;

procedure TeMeSCommandServer.pSetEncryptMessages(aValue: Boolean);
begin
  if aValue then
  begin
    if Assigned(EncryptionAlgorithm) and Assigned (DecryptionAlgorithm) then
      FEncryptMessages := true
    else
      Raise Exception.Create(cExNoAlgorithImplements);
  end else
    FEncryptMessages := false;
end;

procedure TeMeSCommandServer.pThreadSafeProcessIncomingCommand;
begin
  self.pProcessIncomingCommand(FThreadSafeID, FThreadSafeClientCmd);
end;

procedure TeMeSCommandServer.pValidateHashedMessage(aMessageAsColumns: TeMeS_Row);
var ValidMsg:Boolean; Hash:TMsgString; //@eMeS Hashing
begin
  Hash := aMessageAsColumns.Column[aMessageAsColumns.Count];
  aMessageAsColumns.RemoveColumn(aMessageAsColumns.Count); //aizv�cam hash, to mums vair�k visp�r nevajadz�s
  ValidMsg := eMeSHashFromString(aMessageAsColumns.AsString+HashPass) = Hash; //ac�mredzamais neticamais
  If not ValidMsg then
    Raise Exception.Create(cExIncorrectHashKey);
end;

end.
