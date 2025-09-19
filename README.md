/** todo 

criar um processo paralelo para verificar quantas paginas sao e ir buscand
**/
Publicação dos artefatos de update
Você precisa hospedar os arquivos que serão atualizados (jar principal e demais) e um config.xml com checksums e baseUri apontando para onde esses arquivos ficam.
Hoje:
config.xml aponta para um caminho de repositório (.../main/) e lista print.jar — isso não é ideal para produção.
generated-config.xml usa https://example.com/... (placeholder).
Recomendado:
Usar GitHub Releases como servidor de updates.
BaseUri: https://github.com/Rubensgol/print/releases/latest/download/ (ou por tag específica).
Publicar no Release:
app.jar (o jar do seu app, com nome estável).
config.xml (gerado com checksums e baseUri correto).
Automatizar no CI a geração e publicação do config
Ajuste no workflow:
Depois de compilar no Windows, subir também o jar (target/*.jar) como artifact (p. ex. app-jar).
No job release (Ubuntu), fazer:
Checkout do repo.
Setup do JDK.
Baixar os artifacts (MSI + jar).
Copiar o jar baixado para ./target/app.jar (nome estável).
Rodar GenerateUpdateConfig com baseUri = https://github.com/Rubensgol/print/releases/download/${{ github.ref_name }}/ para escrever generated-config.xml.
Publicar no Release:
O .msi (já está).
O app.jar.
O generated-config.xml (renomeie para config.xml na hora de publicar ou mantenha o nome mas aponte o app para ele).
URL do config no aplicativo
Hoje, test.Program tenta ler:
Remoto: https://github.com/rubensgolSecret/print/raw/refs/heads/main/src/config/config.xml
Fallback local: config.xml
Recomendado:
Ler primeiro de: https://github.com/Rubensgol/print/releases/latest/download/config.xml
Fallback local continua válido.
Isso garante que o app sempre busque a última versão publicada sem trocar a URL a cada tag.
Permissões/Path de update
basePath padrão ${user.dir}/ pode apontar para a pasta de instalação (ex.: Program Files). Sem privilégios, o app não conseguirá escrever lá.
Estratégias:
Instalação por usuário (jpackage normalmente instala em %LOCALAPPDATA%/Programs), onde o usuário costuma ter permissão — pode funcionar direto.
Ou definir basePath para pasta do usuário (ex.: ${user.home}/AppData/Local/print/app) para garantir escrita.
Caso queira “bootstrapper” + “app real” (padrão update4j), podemos deixar o bootstrap no Program Files e o app em pasta do usuário; o bootstrap só atualiza a pasta do app.
Lançamento pós-update
Você chama config.launch(), mas seu config.xml não tem uma seção <launch> nem define main.class.
Para relançar corretamente após update:
Uma das opções:
Colocar <launch class="test.Program" /> no config.xml e garantir que o jar listado como classpath="true" seja o app.jar.
Ou definir a propriedade main.class e manter o classpath via classpath="true" nos arquivos.
Sem isso, config.launch() pode não iniciar nada.
O que eu proponho fazer já (próximos passos)