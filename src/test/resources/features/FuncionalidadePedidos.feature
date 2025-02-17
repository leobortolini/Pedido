# language: pt

Funcionalidade: Gerenciamento de Pedidos

  Cenario: Criar um novo pedido
    Quando criar novo pedido
    Então o sistema deve retornar o pedido criado

  Cenario: Receber resposta do estoque
    Dado que exista um pedido enviado para o estoque
    Quando o estoque enviar resposta
    Entao o pedido deve aguardar pagamento

  Cenario: Iniciar um pagamento
    Dado um pedido pendente de pagamento
    Quando o cliente inicia o pagamento para o pedido
    Entao o sistema deve retornar os detalhes do pagamento

  Cenario: Completar um pagamento
    Dado um pedido com pagamento pendente
    Quando completar o pagamento
    Entao o pedido deve ser enviado para logistica
    E o pagamento ser finalizado

  Cenario: Receber resposta da logistica
    Dado um pedido enviado para logistica
    Quando a logistica enviar uma resposta
    Entao o pedido deve ser atualizado

  Cenario: Buscar um pedido pelo ID
    Dado que exista um pedido
    Quando o cliente solicita o pedido pelo ID
    Então o sistema deve retornar os detalhes do pedido
