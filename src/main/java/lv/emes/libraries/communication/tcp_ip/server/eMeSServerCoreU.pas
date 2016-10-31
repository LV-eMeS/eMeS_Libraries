unit eMeSServerCoreU;

interface

uses
  Classes, SysUtils, Windows,
  //servera lietas
  {IdServerIOHandlerStack,} IdTCPServer, IdCustomTCPServer, IdGlobal,
  IdSocketHandle, IdContext,
  eMeS_ListU, eMeSClientServerConstantsU;

type
  TClientOfServer = class
    private
      FID:TUserIDType;
      FIP:String;
    public
      constructor Create(ID:TUserIDType; IP:String);
      property ID:TUserIDType read FID write FID;
      property IP:String read FIP write FIP;
  end;

  TeMeS_ClientList = class(TeMeS_List<TClientOfServer>)
    public
      function GetClient(ClientID:TUserIDType):TClientOfServer;
      procedure RemoveClient(ClientID:TUserIDType);
  end;

  { TeMeSServerCore }

  TProcExecClientCommand = procedure(ClientID: TUserIDType; Command: TMsgString) of object;
  //papildus procedūru šabloni, ja izmantosim SetMethodImplements2
  TProcClientWithIDnIP = procedure(ClientID: TUserIDType; ClientIP:TMsgString) of object;
  TProcClientWithID = procedure(ClientID: TUserIDType) of object;
  TProcClientWithIDnException = procedure(ClientID: TUserIDType; AException:Exception) of object;
  TSimpleProcedure = procedure of object;

  TeMeSServerCore = class
    private
      FServerSocket: TIdTCPServer;
    protected
      FClientList:TeMeS_ClientList;
      FOnExecuteAdd:TProcExecClientCommand;
      //ja ir nepieciešamība nemaz neizmantot servera komponentes ārpus šī unita
      FOnConnectAdd:TProcClientWithIDnIP;
      FOnDisconnectAdd:TProcClientWithID;
      FOnExceptionAdd:TProcClientWithIDnException;
      procedure ServerSocketConnect(AContext: TIdContext); virtual;
      procedure ServerSocketDisconnect(AContext: TIdContext); virtual;
      procedure ServerSocketException(AContext: TIdContext; AException: Exception); virtual;
      procedure ServerSocketExecute(AContext: TIdContext); virtual;
      //implementē 4 galvenās metodes daļēji
      procedure SetMethodImplements(Con, Dis: TIdServerThreadEvent; //OBSOLETE
        Excep:TIdServerThreadExceptionEvent; Exec:TProcExecClientCommand);
      procedure SetMethodImplements2(Con:TProcClientWithIDnIP; //OBSOLETE
        Dis: TProcClientWithID;
        Excep:TProcClientWithIDnException;
        Exec:TProcExecClientCommand);
      function pReadlnMessage(AContext: TIdContext):string;
      procedure pWritelnMessage(aMsg:String; const aItem:TIdContext);
    public
      OnClientConnecting:TSimpleProcedure;
      OnClientDisconnecting:TSimpleProcedure;
      constructor Create(AOwner:TComponent);
      destructor Destroy; override;
      procedure StartServer(Port: TIdPort);
      procedure StopServer;
      procedure DisconnectAllClients;
      procedure DisconnectClientByID(ID: TUserIDType);
      procedure MsgToAll(Msg: TMsgString);
      procedure MsgToClientByID(Msg: TMsgString; ID: TUserIDType);
      function IsServerRunning:Boolean;
      property OnClientConnect:TProcClientWithIDnIP read FOnConnectAdd write FOnConnectAdd;
      property OnClientDisconnect:TProcClientWithID read FOnDisconnectAdd write FOnDisconnectAdd;
      property OnException:TProcClientWithIDnException read FOnExceptionAdd write FOnExceptionAdd;
      property OnIncomingCommand:TProcExecClientCommand read FOnExecuteAdd write FOnExecuteAdd;
      property Clients:TeMeS_ClientList read FClientList;
  end;

implementation

uses IdSchedulerOfThread; //Thread ID iegūšanai

{ TeMeSServerCore }

constructor TeMeSServerCore.Create(AOwner: TComponent);
begin
  FServerSocket := TIdTCPServer.Create(AOwner);
  SetMethodImplements2(nil, nil, nil, nil);
  FClientList := TeMeS_ClientList.Create(True);
  OnClientConnecting := nil;
  OnClientDisconnecting := nil;
end;

destructor TeMeSServerCore.Destroy;
begin
  FClientList.Free;
  inherited Destroy;
end;

procedure TeMeSServerCore.StartServer(Port:TIdPort);
var
  Binding:TIdSocketHandle;
begin
  FServerSocket.Bindings.Clear;
  Binding := FServerSocket.Bindings.Add;
  Binding.Port := Port;
  FServerSocket.Active := True;
end;

procedure TeMeSServerCore.StopServer;
begin
  DisconnectAllClients;
end;

procedure TeMeSServerCore.SetMethodImplements(Con, Dis: TIdServerThreadEvent; Excep:TIdServerThreadExceptionEvent; Exec:TProcExecClientCommand);
begin
  FServerSocket.OnConnect:=Con;
  FServerSocket.OnDisconnect:=Dis;
  FServerSocket.OnException:=Excep;
  FOnExecuteAdd := Exec; //procedūra, kas tiks izsaukta iekš OnExecute
  FServerSocket.OnExecute := ServerSocketExecute;
end;

procedure TeMeSServerCore.SetMethodImplements2(Con: TProcClientWithIDnIP;
  Dis: TProcClientWithID; Excep: TProcClientWithIDnException;
  Exec: TProcExecClientCommand);
begin
  FOnConnectAdd := Con;
  FServerSocket.OnConnect:=ServerSocketConnect;
  FOnDisconnectAdd := Dis;
  FServerSocket.OnDisconnect := ServerSocketDisconnect;
  FOnExceptionAdd := Excep;
  FServerSocket.OnException:=ServerSocketException;
  FOnExecuteAdd := Exec;
  FServerSocket.OnExecute := ServerSocketExecute;
end;

procedure TeMeSServerCore.ServerSocketExecute(AContext: TIdContext);
var
  ID:TUserIDType;
  ClientCmd:TMsgString;
begin
  ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
  //reāli čeko, vai lietotājs ir pieslēdzies, katras 9 sekundes apstājas šeit
  //AContext.Connection.IOHandler.ReadTimeout  := 9000;
  ClientCmd := pReadlnMessage(AContext); //AContext.Connection.IOHandler.ReadLn(TEncoding.Unicode);

  if Assigned(FOnExecuteAdd) then FOnExecuteAdd(ID, ClientCmd); //apstrādā katra tipa komandu
end;

procedure TeMeSServerCore.ServerSocketConnect(AContext: TIdContext);
var
  ID:TUserIDType;
  IP:String;
begin
  ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
  IP := AContext.Connection.Socket.Binding.PeerIP;

  FClientList.Add(TClientOfServer.Create(ID, IP));

  if Assigned(FOnConnectAdd) then FOnConnectAdd(ID, IP);
  if Assigned(OnClientConnecting) then OnClientConnecting;
end;

//ja klients atslēdzas no servera
procedure TeMeSServerCore.ServerSocketDisconnect(AContext: TIdContext);
var
  ID:TUserIDType;
begin
  ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;
  if Assigned(FOnDisconnectAdd) then FOnDisconnectAdd(ID);
  Self.FClientList.RemoveClient(ID);
  Self.FClientList.GetClient(ID).Free;
  if Assigned(OnClientDisconnecting) then OnClientDisconnecting;
end;

procedure TeMeSServerCore.ServerSocketException(AContext: TIdContext;
  AException: Exception);
var
  ID:TUserIDType;
begin
  ID := TIdYarnOfThread(AContext.Yarn).Thread.ThreadID;

  if not IsServerRunning then exit;
  if Assigned(FOnExceptionAdd) then FOnExceptionAdd(ID, AException);
end;

procedure TeMeSServerCore.DisconnectClientByID(ID:TUserIDType);
var lst:TList;
    itm:TIdContext;
    i:Integer;
begin
    try
      lst:=FServerSocket.Contexts.LockList();
      for i:=0 to lst.Count-1 do
      begin
        itm:=TIdContext(lst.Items[i]);
        if Assigned(itm) then
          if TIdYarnOfThread(itm.Yarn).Thread.ThreadID=ID then
          begin
            itm.Connection.Disconnect();
            break;
          end;
      end;
    finally
      FServerSocket.Contexts.UnlockList();
    end;
end;

function TeMeSServerCore.IsServerRunning: Boolean;
begin
  Result := FServerSocket.Active;
end;

procedure TeMeSServerCore.DisconnectAllClients;
var lst:TList; itm:TIdContext; i:Integer;
begin
  //atvienojam visus klientus
  try
    lst:=FServerSocket.Contexts.LockList();
    for i:=0 to lst.Count-1 do
    begin
      itm:=TIdContext(lst.Items[i]);
      if Assigned(itm) then
      begin
           itm.Connection.IOHandler.InputBuffer.Clear;
           itm.Connection.Disconnect();
      end;
    end;
  finally
    FServerSocket.Contexts.UnlockList();
  end;
end;

procedure TeMeSServerCore.MsgToClientByID(Msg:TMsgString; ID:TUserIDType);
var lst:TList; itm:TIdContext; i:Integer;
begin
  If Length(Msg)=0 then exit;
  try
    lst:=FServerSocket.Contexts.LockList();
    for i:=0 to lst.Count-1 do
    begin
      itm:=TIdContext(lst.Items[i]);
      if Assigned(itm) then
         if TIdYarnOfThread(itm.Yarn).Thread.ThreadID=ID then
         begin
           pWritelnMessage(Msg, itm);
           break;
         end;
    end;
  finally
    FServerSocket.Contexts.UnlockList();
  end;
end;

function TeMeSServerCore.pReadlnMessage(AContext: TIdContext): string;
begin
  Result := AContext.Connection.IOHandler.ReadLn(TEncoding.UTF8);
  //Result := AContext.Connection.IOHandler.ReadLn();
end;

procedure TeMeSServerCore.pWritelnMessage(aMsg: String; const aItem:TIdContext);
begin
  aItem.Connection.IOHandler.WriteLn(aMsg, TEncoding.UTF8);
  //aItem.Connection.IOHandler.WriteLn(aMsg);
end;

procedure TeMeSServerCore.MsgToAll(Msg:TMsgString);
var lst:TList; itm:TIdContext; i:Integer;
begin
  If Length(Msg)=0 then exit;
  try
    //threadu saraksts (klientu pieslēgumi)
    lst:=FServerSocket.Contexts.LockList();
    for i:=0 to lst.Count-1 do
    begin
      itm := TIdContext(lst.Items[i]); //klientu konteksts
      if Assigned(itm) then
        pWritelnMessage(Msg, itm);
    end;
  finally
    FServerSocket.Contexts.UnlockList();
  end;
end;

{ TClientOfServer }

constructor TClientOfServer.Create(ID: TUserIDType; IP: String);
begin
  FID:=ID;
  FIP:=IP;
end;

{ TeMeS_ClientList<T> }

function TeMeS_ClientList.GetClient(ClientID: TUserIDType): TClientOfServer;
begin
  Result := nil;
  self.First;
  while Self.NotAtTheEndOfList do
  begin
    if TClientOfServer(self.Current).FID = ClientID then
    begin
      Result := TClientOfServer(self.Current);
      exit;
    end;
    self.Next;
  end;
end;

procedure TeMeS_ClientList.RemoveClient(ClientID: TUserIDType);
begin
  self.First;
  while Self.NotAtTheEndOfList do
  begin
    if TClientOfServer(self.Current).FID = ClientID then
    begin
      self.Remove(TClientOfServer(self.Current));
      exit;
    end;
    self.Next;
  end;
end;

end.

