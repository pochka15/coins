declare type Task = {
  id: number
  title: string
  content: string
  deadline: string
  budget: number
  status: string
  author: string
  authorUserId: string
  assignee: string | null
  assigneeUserId: string | null
}

declare type TNewTask = {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
  userId: string
}
