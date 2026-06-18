-- Database Seed Data for Absolute Cinema

-- Insert 1 Dummy User
INSERT INTO user (username, password, role) VALUES ('johndoe', 'password123', 'USER');

-- Insert Tayangan (Parent Table)
-- IDs will be auto-generated sequentially (1, 2, 3)
INSERT INTO tayangan (judul, sinopsis, tahun_rilis, total_skor, jumlah_reviewer) VALUES 
('The Walking Dead', 'Kisah bertahan hidup di tengah zombie apocalypse.', 2010, 0, 0),
('Capitol Cinema', 'Film dokumenter tentang bioskop klasik.', 2021, 0, 0),
('Classic Films', 'Kumpulan film hitam putih terbaik sepanjang masa.', 1995, 0, 0);

-- Insert into Anak Class (Film)
-- Matching IDs 2 and 3 from the Tayangan table
INSERT INTO film (id, durasi_menit, genre, sutradara) VALUES 
(2, 120, 'Documentary', 'Christopher Nolan'),
(3, 150, 'Drama', 'Steven Spielberg');

-- Insert into Anak Class (SerialTV)
-- Matching ID 1 from the Tayangan table
INSERT INTO serialtv (id, jumlah_musim, total_episode, jaringan_tv, masih_berjalan) VALUES 
(1, 11, 177, 'AMC', false);
