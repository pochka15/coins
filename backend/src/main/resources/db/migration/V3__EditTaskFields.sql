alter table tasks
    alter column deadline drop not null;

alter table tasks
    add solution_note varchar;