delete from Plies;
delete from Ply;

insert into plies  ( id, config_string, user_name, started, current_ply_Number, begonnen ) 
	values( 1, 'KDKT', 'purbanus', '2025-05-14 13:15:00', 2, true );
insert into ply    ( id, plies_id, einde, zet_Nummer, schaak, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten )  
	values( 1, 1, 'Nog_niet', 1, false, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34 );
insert into ply    ( id, plies_id, einde, zet_Nummer, schaak, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten )  
	values( 2, 1, 'Nog_niet', 2, false, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34 );
insert into plies  ( id, config_string, user_name, started, current_ply_Number, begonnen ) 
	values( 2, 'KDKT', 'purbanus', '2025-05-14 12:15:00', 2, true );
insert into ply    ( id, plies_id, einde, zet_Nummer, schaak, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten )  
	values( 3, 2, 'Nog_niet', 1, false, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34 );
insert into ply    ( id, plies_id, einde, zet_Nummer, schaak, van, naar, wk, zk, s3, s4, s5, aan_zet, resultaat, aantal_zetten )  
	values( 4, 2, 'Nog_niet', 2, false, 17, 85, 00, 119, 17, 102, 00, 'Wit', 'Gewonnen', 34 );

 alter sequence hibernate_sequence restart with 500
