declare type Task = {
  id: number
  title: string
  content: string
  deadline: string
  budget: number
  status: string
}

declare type TNewTask = {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
  userId: string
}
