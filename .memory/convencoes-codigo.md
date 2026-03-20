CONVENÇÕES DE CÓDIGO DELPHI
============================

--- LOGS (LogarDebug) ---

1. Sempre usar acentuação direta em português do Brasil (Início, Posição, concluído, período)
   NUNCA usar caracteres codificados (#231, #227, #237, etc.)

2. Quando o log é quebrado em múltiplas linhas, separar a posição na própria linha:
   CORRETO:
     'Posição 3 | ' +
     'Produto [' + IntToStr(x) + '] ' +
     'Período [' + DateToStr(y) + ']'

   ERRADO:
     'Posição 3 | Produto [' + IntToStr(x) + '] ' +
     'Período [' + DateToStr(y) + ']'

3. Posições devem ser numeradas sequencialmente dentro de cada método

4. Ao adicionar um log antes de um Exit, renumerar as posições seguintes

--- SQL (DAO) ---

1. Usar alias PascalCase nos campos do SELECT (as IdAcordo, as CodigoProduto, as DataInicio)
2. FieldByName deve referenciar a alias, não o nome da coluna do banco
3. Não usar alias quando o campo já tem nome claro e não é exposto ao VO

--- MÉTODOS ---

1. Preferir métodos da classe em vez de submétodos (nested procedures)
   Exceção: funções muito específicas do processo (ex: LocalizarItemGrid)

2. Parâmetros de objeto que serão passados como out/var para outros métodos
   não devem ser declarados como const (Delphi impede const -> out)

--- VO (Value Objects) ---

1. Criar em unit separada com nome correspondente ao conceito
2. Incluir ToString com todos os campos
3. Incluir tipo lista (TObjectList<T>) na mesma unit quando necessário
4. Variáveis locais de VO usam prefixo `oVO` (ex: oVOEmpresa, oVONotificacao)
5. Listas e dicionários NÃO usam prefixo `VO` (ex: oListaPorPeriodo, ADicionarioEmpresa)
6. NUNCA usar replace_all para renomear variáveis curtas que podem ser substring de outros identificadores

--- ENCODING ---

1. Arquivos .pas Delphi devem ser ANSI (Windows-1252), NÃO UTF-8
2. Ao editar com Edit tool, acentos ficam em UTF-8 → converter com:
   iconv -f UTF-8 -t WINDOWS-1252 arquivo.pas > /tmp/arquivo.pas && cp /tmp/arquivo.pas arquivo.pas
3. Verificar encoding com: file arquivo.pas (deve ser ISO-8859, não UTF-8)

--- PROJETO DLL ---

1. Novas units devem ser adicionadas tanto no .dpr quanto no .dproj
2. A ordem das DCCReference no .dproj importa: dependências antes dos dependentes
3. Build: D:\dsv-git\dsv-delphi\sysmos1-modular\Comercial\AcordoComercial\SellOut\src\build\Build.bat
4. Compilar via: cmd.exe /c "caminho\Build.bat" 2>&1
