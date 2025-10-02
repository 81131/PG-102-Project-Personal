---------------------------------
-- APPETIZERS (category_id = 1)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Stuffed Mushroom', 'Mushrooms stuffed with herbed cheese and baked to perfection.', 1, 8.50, 1);
DECLARE @StuffedMushroomId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@StuffedMushroomId, '/images/stuffed-mushroom.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Bruschetta', 'Toasted bread topped with fresh tomatoes, basil, and olive oil.', 1, 7.00, 1);
DECLARE @BruschettaId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@BruschettaId, '/images/bruschetta.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Spring Rolls', 'Crispy vegetable spring rolls served with sweet chili sauce.', 1, 6.50, 1);
DECLARE @SpringRollsId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@SpringRollsId, '/images/spring-rolls.jpg');

---------------------------------
-- SOUPS (category_id = 2)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Tomato Basil Soup', 'Smooth tomato soup with a touch of basil.', 1, 5.00, 2);
DECLARE @TomatoBasilSoupId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@TomatoBasilSoupId, '/images/tomato-basil-soup.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Chicken Noodle Soup', 'Classic chicken noodle soup made with fresh herbs.', 1, 5.50, 2);
DECLARE @ChickenNoodleSoupId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@ChickenNoodleSoupId, '/images/chicken-noodle-soup.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Mushroom Soup', 'Creamy mushroom soup topped with fresh herbs.', 1, 5.25, 2);
DECLARE @MushroomSoupId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@MushroomSoupId, '/images/mushroom-soup.jpg');

---------------------------------
-- SALADS (category_id = 3)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Caesar Salad', 'Crisp romaine lettuce tossed with Caesar dressing and croutons.', 1, 7.25, 3);
DECLARE @CaesarSaladId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@CaesarSaladId, '/images/caesar-salad.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Greek Salad', 'Fresh cucumbers, tomatoes, olives, and feta cheese.', 1, 7.50, 3);
DECLARE @GreekSaladId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@GreekSaladId, '/images/greek-salad.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Quinoa Salad', 'Healthy quinoa salad with roasted veggies and a light vinaigrette.', 1, 8.00, 3);
DECLARE @QuinoaSaladId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@QuinoaSaladId, '/images/quinoa-salad.jpg');

---------------------------------
-- MAIN COURSE (category_id = 4)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Grilled Salmon', 'Fresh salmon fillet grilled and served with lemon butter sauce.', 1, 14.50, 4);
DECLARE @GrilledSalmonId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@GrilledSalmonId, '/images/grilled-salmon.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Chicken Alfredo Pasta', 'Creamy Alfredo pasta topped with grilled chicken.', 1, 12.00, 4);
DECLARE @ChickenAlfredoPastaId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@ChickenAlfredoPastaId, '/images/chicken-alfredo-pasta.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Beef Steak', 'Juicy sirloin steak grilled to perfection, served with garlic butter.', 1, 18.00, 4);
DECLARE @BeefSteakId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@BeefSteakId, '/images/beef-steak.jpg');

---------------------------------
-- DESSERTS (category_id = 5)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Chocolate Lava Cake', 'Warm chocolate cake with a gooey molten center.', 1, 6.00, 5);
DECLARE @ChocolateLavaCakeId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@ChocolateLavaCakeId, '/images/chocolate-lava-cake.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Cheesecake', 'Classic creamy cheesecake topped with berry compote.', 1, 5.50, 5);
DECLARE @CheesecakeId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@CheesecakeId, '/images/cheesecake.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Tiramisu', 'Italian dessert made with coffee-soaked ladyfingers and mascarpone cream.', 1, 5.75, 5);
DECLARE @TiramisuId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@TiramisuId, '/images/tiramisu.jpg');

---------------------------------
-- BEVERAGES (category_id = 6)
---------------------------------
INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Iced Lemon Tea', 'Refreshing lemon-flavored iced tea served chilled.', 1, 3.50, 6);
DECLARE @IcedLemonTeaId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@IcedLemonTeaId, '/images/iced-lemon-tea.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Mango Smoothie', 'Creamy mango smoothie with a hint of honey.', 1, 4.50, 6);
DECLARE @MangoSmoothieId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@MangoSmoothieId, '/images/mango-smoothie.jpg');

INSERT INTO catalogue_items (name, description, serving_size_person, price, category_id)
VALUES ('Cappuccino', 'Classic Italian-style cappuccino with frothy milk.', 1, 4.00, 6);
DECLARE @CappuccinoId BIGINT = SCOPE_IDENTITY();
INSERT INTO catalogue_item_photos (item_id, photo_url)
VALUES (@CappuccinoId, '/images/cappuccino.jpg');
