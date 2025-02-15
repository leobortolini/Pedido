INSERT INTO pedidos.cliente
(id, cep, cpf, endereco, nome)
VALUES(1, '95180-000', '000.000.000-00', 'endereco', 'Leonardo Bortolini');


INSERT INTO pedidos.pedido
(cliente_id, id, status)
VALUES(1, 1, 'ENVIADO_EXPEDICAO');

INSERT INTO pedidos.pagamento
(valor, id, pedido_id, status)
VALUES(30.00, 1, 1, 'FINALIZADO');


INSERT INTO pedidos.compra_produto
(preco, quantidade, id, pedido_id, produto_id)
VALUES(10.00, 3, 1, 1, 1);