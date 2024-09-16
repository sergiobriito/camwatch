# CamWatch

## Visão Geral

**CamWatch** é um sistema centralizado de monitoramento e gerenciamento de câmeras. Ele oferece uma interface amigável para gerenciar dispositivos, visualizar seus status.
## Arquitetura
O sistema é projetado com uma arquitetura multinível composta pelos seguintes componentes:

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java com Spring Framework
- **Banco de Dados**: PostgreSQL
- **Agente**: Python

### Frontend
O frontend é construído usando HTML, CSS e JavaScript. Ele fornece uma interface intuitiva para os usuários interagirem com o sistema. As principais funcionalidades incluem:

- **Dashboard**: Exibe uma lista de dispositivos, seus status e outras informações relevantes.
- **Gerenciamento de Usuários**: Gerencia sessões de usuários e autenticação.
- **Design Responsivo**: Garante compatibilidade em vários dispositivos e tamanhos de tela.

#### Tecnologias Utilizadas
- **HTML5**: Estruturação das páginas da web.
- **CSS3**: Estilização da interface do usuário.
- **JavaScript**: Proporciona funcionalidades dinâmicas.

### Backend
O backend é desenvolvido usando Java com o Spring Framework. Ele lida com a lógica de negócios, interações com o banco de dados e comunicação com o frontend. O backend também fornece APIs para interação com o agente.

#### Principais Funcionalidades
- **APIs RESTful**: Expõe endpoints para gerenciamento de dispositivos, autenticação de usuários e mais.
- **Spring Boot**: Simplifica a configuração da aplicação com suporte a servidor embutido.
- **Segurança**: Spring Security integrado para lidar com autenticação e autorização.

### Banco de Dados
O GoPresence utiliza o PostgreSQL como seu principal sistema de gerenciamento de banco de dados. Ele armazena informações sobre usuários, dispositivos e seus status.

#### Principais Aspectos
- **Design do Esquema**: Otimizado para recuperação rápida e armazenamento eficiente dos dados dos dispositivos.
- **UUID**: Utilizado para identificação única de registros.
- **Transações**: Garantem integridade e consistência dos dados.

### Agente
O agente CamWatch é um script Python que roda em dispositivos para coletar e enviar dados de volta ao servidor. Ele é responsável por:

- **Registro do Dispositivo**: Registra o dispositivo no servidor GoPresence.
- **Monitoramento de Status**: Verifica periodicamente o status do dispositivo e o reporta ao servidor.

#### Tecnologias Utilizadas
- **Python 3.x**: A principal linguagem usada para desenvolver o agente.
- **Requests Library**: Para fazer requisições HTTP ao servidor GoPresence.
- **JSON**: Para serialização de dados e comunicação com o backend.
