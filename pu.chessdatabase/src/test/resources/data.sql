delete from Plies;
delete from Ply;

insert into plies  ( id, config_string, user_name, started, current_ply_nummer, begonnen, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak ) 
	values( 1, 'KDKT', 'purbanus', '2025-05-14 13:15:00', 2, true, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );
insert into ply    ( id, plies_id, einde, ply_nummer, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak )  
	values( 1, 1, 'Nog_niet', 0, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );
insert into ply    ( id, plies_id, einde, ply_nummer, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak )  
	values( 2, 1, 'Nog_niet', 1, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );
insert into plies  ( id, config_string, user_name, started, current_ply_nummer, begonnen, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak ) 
	values( 2, 'KDKT', 'purbanus', '2025-05-14 12:15:00', 2, true, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );
insert into ply    ( id, plies_id, einde, ply_nummer, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak )  
	values( 3, 2, 'Nog_niet', 0, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );
insert into ply    ( id, plies_id, einde, ply_nummer, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten, schaak )  
	values( 4, 2, 'Nog_niet', 1, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34, false );

 alter sequence hibernate_sequence restart with 500
