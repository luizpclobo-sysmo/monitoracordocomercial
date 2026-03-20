# Memoria Persistente do Projeto

Este diretorio (.memory/) e a memoria persistente do projeto.
Consulte este arquivo ao iniciar cada sessao.

## Estrutura

.memory/
  memory.md      <- Este arquivo (instrucoes de uso)
  .session/      <- Conhecimento persistente do projeto, organizado por assunto
  .scripts/      <- Scripts gerados durante o trabalho
  .code/         <- Convencoes e padroes de codigo
  .temp/         <- Arquivos temporarios

## Regras de uso

1. **Inicio de sessao**: Leia este arquivo. Em seguida, leia AUTOMATICAMENTE o .session/index (se existir) para carregar o contexto completo do projeto — NAO pergunte ao usuario se deseja ler, apenas leia.
2. **Durante o trabalho**:
   - Scripts gerados -> salvar em .memory/.script/
   - Arquivos temporarios -> salvar em .memory/.temp/
   - NUNCA criar arquivos soltos na raiz do projeto
3. **Fim de sessao ou ao concluir algo importante**:
   - Salvar descobertas, decisoes, resolucoes e erros em .session/
   - Atualizar .session/index com o estado atual
4. **Criar arquivos em .session/ por assunto** (sem extensao), ex:
   - index (indice principal, estado atual, conexoes)
   - schema-banco (modelo de dados)
   - regras-negocio (regras do dominio)
   - resolucoes (problemas encontrados e solucoes)
   - regras-usuario (preferencias do usuario)
   - [criar novos conforme necessario]
5. **Indexar sempre**: Ao criar um novo arquivo em .session/, adicionar entrada no index.
6. **Sem caminhos fixos**: Usar caminhos relativos nos arquivos de memoria.

## O que salvar

- Decisoes de arquitetura e design
- Schema de banco de dados e APIs
- Resolucao de problemas (erro, causa, solucao, status)
- Preferencias e regras do usuario
- Conexoes e credenciais de trabalho
- Estado atual do projeto (o que foi feito, o que falta)

## O que NAO salvar

- Codigo fonte (ja esta no repositorio)
- Informacao que muda a cada execucao (logs, outputs)
- Dados sensiveis de producao
