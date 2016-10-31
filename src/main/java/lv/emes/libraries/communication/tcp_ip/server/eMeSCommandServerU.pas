unit eMeSCommandServerU;

interface

uses eMeSServerCoreU, eMeS_Table_Row_HandlerU, eMeS_ListU, eMeSCommandU, eMeSClientServerConstantsU,
     Classes, Sysutils, IdContext, IdSchedulerOfThread;

type
  { Nepiecieðamie juniti, lai pilnvçrtîgi izmantotu ðo klasi:
    * eMeSClientServerConstantsU
    * eMeSCommandU
    * eMeS_Table_Row_HandlerU

    Izmanto, lai sûtîtu specifiskas komandas klientiem, kâ arî saòemtu tâda paða formâta komandas.
    Komandas iespçjams ðifrçt ar iepriekðdefinçtu ðifrçðanas algoritmu, kas jâdefinç, uzstâdot
    `EncryptionAlgorithm un `DecryptionAlgorithm, kâ arî aktivizçjot `EncryptMessages := true;
    Lîdz ar to, visas izejoðâs ziòas tiks ðifrçtas izmantojot ðos algoritmus.

    //ârçjâs brîvâs procedûras
    function TestEncryptionAlgorithm(text:string):String;
    begin
      Result := '...Some kind of algorithm here to encrypt text...';
    end;
    function TestDecryptionAlgorithm(text:string):String;
    begin
      Result := '...Some kind of algorithm here to decrypt text...';
    end;
    procedure TestRegCommand(const aCmdData:TeMeS_Row; aSenderID:TUserIDType=cNoIDValue);
    //CmdData.ID ir saglabâts komandas numurs
    begin
      //aCmdData domâts tikai lasîðanai, ja datus ârpus ðîs procedûras kkur jâizmanto,
      //tad tos jânokopç, jo pçc tam objekts aCmdData tiek iznîcinâts automâtiski (tas nav procedûrâ TestRegCommand paðam jâatbrîvo)
      Showmessage('User ID = '+IntToStr(aSenderID)+#13+
        'Command data is: ' + aCmdData.AsString(' '));
    end;

    Server := TeMeSCommandServer.Create(self);
    Server.EncryptionAlgorithm := TestEncryptionAlgorithm;
    Server.DecryptionAlgorithm := TestDecryptionAlgorithm;
    Server.EncryptMessages := true;

    `MsgToAll un `MsgToClientByID principâ nevajadzçtu izmantot komandu serverim, ja vienîgi tâs nav
    domâtas, lai vienkârði ðifrçtu ziòu. Principâ `ListeningMode nosaka, kâ darbojas komandu serveris. Ir divi varianti:
    1) lmMessages reþîms padara to par principâ tâdu paðu serveri kâ TeMeSServerCore klasç aprakstîto. Ziòas tiek sûtîtas,
       apstrâdâtas un saòemtas gluþi tâpat. Arî `OnIncomingCommand events tiek izpildîts tâpat kâ iepriekð.
    2) lmCommands reþîms visu apgrieþ kâjâm gaisâ. ziòas tiek saòemtas un izpildîtas kâ komandas. `OnIncomingCommand tiek ignorçts, jo
       visas iespçjamâs komandas jau ir reìistrçtas iekð `CommandList saraksta. Tâs arî automâtiski tiek izpildîtas pçc instrukcijas.
       Ja komanda nav atrasta, tad ienâkoðâ ziòa vienkârði tiek ignorçta.
    Ðim objektam ðo metoþu vietâ bûs `CmdToAll un `CmdToClientByID,
    kur kâ parametri bûs jâpadod komanda kâ TeMeS_Row objekts, kura pirmâ kolonna bûs komandas kârtas numurs jeb ID, kâ
    pârçjie pârçjie dati, kas nepiecieðami komandas apstrâdei. Otrais parametrs ir jânodod teksta atdalîtâj simbols, kas
    noteiks, kâds simbols tekstâ atdala vienu kolonnu no otras. P.S. TeMeS_Row paðam ir jârada un jâatbrîvo, metodes
    to izmanto tikai datu iegûðanai.


    Server.ListeningMode := lmCommands;
    Server.RegisterNewCommand(1, TestRegCommand);
    //(...) ðeit reìistrç arî pârçjâs atpazîstamâs komandas, kuras tiks sûtîtas serverim

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
  aCmdData.AddColumn(aCmdCode); //komandas kods bûs pats pçdçjais virknes elements
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
  pHashMessage(Msg); //ja nepiecieðama haðçðana, darîsim to! @eMeS Hashing
  if self.FEncryptMessages then
    Msg := self.FEncryptionAlgorithm(Msg);
  inherited;
end;

procedure TeMeSCommandServer.MsgToClientByID(Msg: TMsgString; ID: TUserIDType);
begin
  pHashMessage(Msg); //ja nepiecieðama haðçðana, darîsim to! @eMeS Hashing
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
      pValidateHashedMessage(CmdData); //ðis izmetîs exception, ja nebûs kkas labi ar haðçðanu //@eMeS Hashing
    aCmdCode := CmdData.GetColumn(CmdData.Count); //pçdçjâ komanda ir komandas kods, pçc kura atpazîsim komandas
  except //fake komandas pat neapskatîsim
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
  if ListeningMode = lmCommands then //ja klausâmies komandas
  begin
    ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
    ClientCmd := pReadlnMessage(AContext);
    if self.FEncryptMessages then //arî komandas iespçjams izvçlçties ðifrçt vai nç
      ClientCmd := self.FDecryptionAlgorithm(ClientCmd);
    try
      FThreadSafeID := ID;
      FThreadSafeClientCmd := ClientCmd;
      TIdYarnOfThread(AContext.Yarn).Thread.Synchronize(pThreadSafeProcessIncomingCommand);
    finally
    end;
  end else //ja klausâmies parastas ziòas
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
  aMessageAsColumns.RemoveColumn(aMessageAsColumns.Count); //aizvâcam hash, to mums vairâk vispâr nevajadzçs
  ValidMsg := eMeSHashFromString(aMessageAsColumns.AsString+HashPass) = Hash; //acîmredzamais neticamais
  If not ValidMsg then
    Raise Exception.Create(cExIncorrectHashKey);
end;

end.
