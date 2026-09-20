# language: pt
Funcionalidade: Consulta de beans e erros do container
  Como desenvolvedor usando o Autumn
  Quero achar bean por tipo ou nome e falhas com tipo e mensagem úteis
  Para usar o contexto sem adivinhar o que deu errado

  Cenário: getBean por classe concreta
    Dado um bean concreto registrado
    Quando eu peço getBean dessa classe
    Então recebo a instância gerenciada

  Cenário: getBean por interface
    Dado uma interface com implementação registrada
    Quando eu peço getBean da interface
    Então recebo a implementação

  Cenário: getBean pelo value de @Component
    Dado um bean com @Component("email")
    Quando eu peço getBean("email")
    Então recebo essa instância

  Cenário: getBean pelo simple name quando não há value
    Dado um bean @Component sem value chamado OrderService
    Quando eu peço getBean("OrderService")
    Então recebo essa instância

  Cenário: value de @Component tem prioridade sobre simple name
    Dado um bean @Component("email") cujo simple name é EmailSender
    Quando eu peço getBean("email")
    Então recebo EmailSender
    E getBean("EmailSender") também encontra o mesmo bean se ninguém mais tomou esse nome

  Cenário: bean inexistente por tipo
    Quando eu peço getBean de um tipo não registrado
    Então lança BeanNotFoundException
    E a mensagem contém o nome do tipo

  Cenário: bean inexistente por nome
    Quando eu peço getBean("naoExiste")
    Então lança BeanNotFoundException
    E a mensagem contém "naoExiste"

  Cenário: containsBean de tipo desconhecido não lança
    Quando eu pergunto containsBean de um tipo não registrado
    Então o resultado é false
    E nenhuma NullPointerException é lançada

  Cenário: close está no ApplicationContext
    Dado um contexto já iniciado
    Quando eu chamo close pela interface ApplicationContext
    Então o shutdown dos singletons roda

  Cenário: logger interno usa prefixo [AUTUMN]
    Quando o container emite info, warn ou error
    Então cada linha começa com "[AUTUMN]"

  Cenário: core não escreve direto em System.out
    Dado o bootstrap de um pacote com componentes
    Então as mensagens do framework passam por AutumnLogger
    E não por System.out.print nem System.err.print no core
